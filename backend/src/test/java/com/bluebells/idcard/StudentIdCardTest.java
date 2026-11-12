package com.bluebells.idcard;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.idcard.dto.GenerateIdCardRequest;
import com.bluebells.idcard.dto.ReissueIdCardRequest;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentIdCardTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AcademicSessionRepository academicSessionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentIdCardRepository studentIdCardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private Long studentId;
    private Long sessionId;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));

        User admin = userRepository.findByUsername("idcard_admin_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("idcard_admin_test");
            u.setEmail("idcard_admin@bluebells.test");
            u.setFullName("ID Card Admin");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        AcademicSession session = academicSessionRepository.findAll().stream().findFirst().orElseGet(() -> {
            AcademicSession s = new AcademicSession();
            s.setName("2025-2026");
            s.setStartDate(LocalDate.of(2025, 8, 1));
            s.setEndDate(LocalDate.of(2026, 6, 30));
            s.setCurrent(true);
            return academicSessionRepository.save(s);
        });
        sessionId = session.getId();

        ClassEntity cls = classRepository.findAll().stream().findFirst().orElseGet(() -> {
            ClassEntity c = new ClassEntity();
            c.setName("Grade 8");
            c.setCode("G8");
            return classRepository.save(c);
        });

        Section sec = sectionRepository.findAll().stream().findFirst().orElseGet(() -> {
            Section s = new Section();
            s.setName("A");
            s.setClassEntity(cls);
            return sectionRepository.save(s);
        });

        Student student = studentRepository.findByAdmissionNumber("BSS-IDC-001").orElseGet(() -> {
            Student s = new Student();
            s.setAdmissionNumber("BSS-IDC-001");
            s.setFirstName("Zain");
            s.setLastName("Abbas");
            s.setDateOfBirth(LocalDate.of(2011, 4, 12));
            s.setGender("MALE");
            s.setBloodGroup("O+");
            s.setBFormNumber("41303-9999999-1");
            s.setResidentialAddress("Autobahn Road, Hyderabad");
            s.setAdmissionDate(LocalDate.of(2024, 8, 1));
            s.setStatus("ACTIVE");
            return studentRepository.save(s);
        });
        studentId = student.getId();

        if (enrollmentRepository.findByStudentIdAndAcademicSessionId(studentId, sessionId).isEmpty()) {
            Enrollment e = new Enrollment();
            e.setStudent(student);
            e.setSection(sec);
            e.setAcademicSession(session);
            e.setRollNumber("12");
            e.setEnrollmentStatus("ACTIVE");
            e.setEnrollmentDate(LocalDate.of(2025, 8, 1));
            enrollmentRepository.save(e);
        }
    }

    @Test
    @DisplayName("Admin can generate digital student ID card with safe QR payload")
    void testGenerateStudentIdCardWithSafeQr() throws Exception {
        GenerateIdCardRequest req = new GenerateIdCardRequest();
        req.setStudentId(studentId);
        req.setAcademicSessionId(sessionId);

        MvcResult result = mockMvc.perform(post("/api/v1/id-cards/generate")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cardNumber").value(startsWith("IDC-")))
                .andExpect(jsonPath("$.data.studentName").value("Zain Abbas"))
                .andExpect(jsonPath("$.data.qrVerificationToken").isString())
                .andExpect(jsonPath("$.data.qrPayload").value(containsString("https://portal.bluebells.edu.pk/verify-id")))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        // Crucial security requirement: QR payload must NOT leak CNIC, address, or sensitive personal data
        org.junit.jupiter.api.Assertions.assertFalse(responseJson.contains("41303-9999999-1"), "QR payload must not contain CNIC or B-Form");
    }

    @Test
    @DisplayName("Admin can reissue lost/damaged ID card with replacement tracking")
    void testReissueStudentIdCard() throws Exception {
        // First ensure a card exists
        GenerateIdCardRequest req = new GenerateIdCardRequest();
        req.setStudentId(studentId);
        req.setAcademicSessionId(sessionId);

        MvcResult genResult = mockMvc.perform(post("/api/v1/id-cards/generate")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long cardId = objectMapper.readTree(genResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        ReissueIdCardRequest reissueReq = new ReissueIdCardRequest();
        reissueReq.setReason("LOST");

        mockMvc.perform(post("/api/v1/id-cards/" + cardId + "/reissue")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reissueReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.cardNumber").value(containsString("-R")))
                .andExpect(jsonPath("$.data.reissueCount").value(greaterThanOrEqualTo(1)));

        // Verify that the original card has been marked as REISSUED
        StudentIdCard oldCard = studentIdCardRepository.findById(cardId).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("REISSUED", oldCard.getStatus());
    }

    @Test
    @DisplayName("Public can safely verify card validity via verification token without credentials")
    void testPublicVerificationEndpoint() throws Exception {
        GenerateIdCardRequest req = new GenerateIdCardRequest();
        req.setStudentId(studentId);
        req.setAcademicSessionId(sessionId);

        MvcResult genResult = mockMvc.perform(post("/api/v1/id-cards/generate")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(genResult.getResponse().getContentAsString()).get("data").get("qrVerificationToken").asText();

        // Public call without Authorization header
        mockMvc.perform(get("/api/v1/id-cards/verify/" + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.studentName").value("Zain Abbas"))
                .andExpect(jsonPath("$.data.message").value(containsString("Bluebells")))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Teacher can generate student ID card")
    void testTeacherCanGenerateStudentIdCard() throws Exception {
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));

        User teacher = userRepository.findByUsername("idcard_teacher_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("idcard_teacher_test");
            u.setEmail("idcard_teacher@bluebells.test");
            u.setFullName("Sir Ahmed Khan");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(teacherRole)));
            return userRepository.save(u);
        });
        String teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        GenerateIdCardRequest req = new GenerateIdCardRequest();
        req.setStudentId(studentId);
        req.setAcademicSessionId(sessionId);

        mockMvc.perform(post("/api/v1/id-cards/generate")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studentName").value("Zain Abbas"));
    }

    @Test
    @DisplayName("Get active card when none exists returns 200 with data null rather than crashing")
    void testGetActiveCardWhenNoneExistsReturnsNull() throws Exception {
        // Create an uncarded student
        Student uncarded = studentRepository.save(new Student());
        uncarded.setAdmissionNumber("BSS-TEMP-999");
        uncarded.setFirstName("Farhan");
        uncarded.setLastName("Ali");
        uncarded.setDateOfBirth(LocalDate.of(2012, 1, 1));
        uncarded.setGender("MALE");
        uncarded.setStatus("ACTIVE");
        uncarded = studentRepository.save(uncarded);

        mockMvc.perform(get("/api/v1/id-cards/students/" + uncarded.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Bulk generate all cards endpoint generates cards across students")
    void testBulkGenerateAllCards() throws Exception {
        mockMvc.perform(post("/api/v1/id-cards/generate-all")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
