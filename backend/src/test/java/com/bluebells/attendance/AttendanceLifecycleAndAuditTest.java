package com.bluebells.attendance;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.attendance.dto.AttendanceCorrectionRequest;
import com.bluebells.attendance.dto.AttendanceItemRequest;
import com.bluebells.attendance.dto.RecordSectionAttendanceRequest;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLink;
import com.bluebells.guardians.GuardianStudentLinkId;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AttendanceLifecycleAndAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository linkRepository;

    @Autowired
    private AttendanceCorrectionRepository correctionRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String teacherToken;
    private String parentAToken;
    private String parentBToken;

    private Section targetSection;
    private Enrollment enrollment1;
    private Enrollment enrollment2;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));

        // Teacher
        User teacher = userRepository.findByUsername("teacher_att_test")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("teacher_att_test");
                    u.setEmail("att.teacher@bluebells.edu.pk");
                    u.setFullName("Class Teacher");
                    u.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(teacherRole));
                    return userRepository.save(u);
                });
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        // Parent A
        User userA = userRepository.findByUsername("parent_att_a")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_att_a");
                    u.setEmail("parent.att.a@test.com");
                    u.setFullName("Parent A Att");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        Guardian guardianA = guardianRepository.findByUserId(userA.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent A Att", "FATHER", "+92 300 9999991", "41303-9999991-1");
                    g.setUser(userA);
                    return guardianRepository.save(g);
                });

        // Parent B
        User userB = userRepository.findByUsername("parent_att_b")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_att_b");
                    u.setEmail("parent.att.b@test.com");
                    u.setFullName("Parent B Att");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentBToken = jwtTokenProvider.generateToken(UserPrincipal.create(userB));

        Guardian guardianB = guardianRepository.findByUserId(userB.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent B Att", "MOTHER", "+92 300 9999992", "41303-9999992-2");
                    g.setUser(userB);
                    return guardianRepository.save(g);
                });

        targetSection = sectionRepository.findAll().get(0);
        AcademicSession session = sessionRepository.findByCurrentTrue().orElseGet(() -> sessionRepository.findAll().get(0));

        student1 = new Student();
        student1.setAdmissionNumber("BSS-ATT-001");
        student1.setFirstName("Bilal");
        student1.setLastName("Ahmed");
        student1.setDateOfBirth(LocalDate.of(2016, 2, 1));
        student1.setGender("MALE");
        student1.setResidentialAddress("Unit 2, Latifabad");
        student1.setAdmissionDate(LocalDate.now());
        student1.setStatus("ACTIVE");
        student1 = studentRepository.save(student1);

        enrollment1 = new Enrollment(student1, session, targetSection, "01", LocalDate.now());
        enrollment1 = enrollmentRepository.save(enrollment1);
        student1.getEnrollments().add(enrollment1);

        student2 = new Student();
        student2.setAdmissionNumber("BSS-ATT-002");
        student2.setFirstName("Fatima");
        student2.setLastName("Noor");
        student2.setDateOfBirth(LocalDate.of(2016, 4, 15));
        student2.setGender("FEMALE");
        student2.setResidentialAddress("Autobahn Road");
        student2.setAdmissionDate(LocalDate.now());
        student2.setStatus("ACTIVE");
        student2 = studentRepository.save(student2);

        enrollment2 = new Enrollment(student2, session, targetSection, "02", LocalDate.now());
        enrollment2 = enrollmentRepository.save(enrollment2);
        student2.getEnrollments().add(enrollment2);

        // Link student 1 to Parent A
        linkRepository.save(new GuardianStudentLink(guardianA, student1, true, true));
        // Link student 2 to Parent B
        linkRepository.save(new GuardianStudentLink(guardianB, student2, true, true));
    }

    @Test
    @DisplayName("Attendance Lifecycle: Mark daily attendance -> Duplicate rejected -> Correct status with audit -> Summary")
    void testAttendanceLifecycleAndAuditTrail() throws Exception {
        LocalDate attDate = LocalDate.now();

        // 1. Teacher marks section attendance (Student 1 = PRESENT, Student 2 = ABSENT)
        RecordSectionAttendanceRequest recordReq = new RecordSectionAttendanceRequest(
                targetSection.getId(),
                attDate,
                List.of(
                        new AttendanceItemRequest(enrollment1.getId(), "PRESENT", "On time"),
                        new AttendanceItemRequest(enrollment2.getId(), "ABSENT", "Uninformed absence")
                )
        );

        MvcResult recordResult = mockMvc.perform(post("/api/v1/attendance/mark")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andReturn();

        Long student2AttId = objectMapper.readTree(recordResult.getResponse().getContentAsString())
                .path("data").get(1).path("id").asLong();

        // 2. Marking attendance for the same section and date again MUST be rejected with 409 Conflict
        mockMvc.perform(post("/api/v1/attendance/mark")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail", containsString("Attendance already recorded")));

        // 3. Parent A views Student 1's attendance history -> 200 OK
        mockMvc.perform(get("/api/v1/attendance/student/" + student1.getId())
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].status", is("PRESENT")));

        // 4. Parent A attempting to view Student 2's attendance MUST receive 403 Forbidden
        mockMvc.perform(get("/api/v1/attendance/student/" + student2.getId())
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isForbidden());

        // 5. Admin / Principal corrects Student 2's status from ABSENT to EXCUSED with audit reason
        AttendanceCorrectionRequest correctionReq = new AttendanceCorrectionRequest(
                "EXCUSED",
                "Medical certificate submitted by guardian and approved by principal"
        );

        mockMvc.perform(put("/api/v1/attendance/" + student2AttId + "/correction")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctionReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("EXCUSED")));

        // 6. Verify audit correction record exists in repository
        List<AttendanceCorrection> corrections = correctionRepository.findByAttendanceId(student2AttId);
        org.junit.jupiter.api.Assertions.assertFalse(corrections.isEmpty());
        org.junit.jupiter.api.Assertions.assertEquals("ABSENT", corrections.get(0).getOldStatus());
        org.junit.jupiter.api.Assertions.assertEquals("EXCUSED", corrections.get(0).getNewStatus());

        // 7. Verify student summary endpoint
        mockMvc.perform(get("/api/v1/attendance/student/" + student1.getId() + "/summary")
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.presentDays", is(1)))
                .andExpect(jsonPath("$.data.absentDays", is(0)))
                .andExpect(jsonPath("$.data.attendancePercentage", is(100.0)));
    }
}
