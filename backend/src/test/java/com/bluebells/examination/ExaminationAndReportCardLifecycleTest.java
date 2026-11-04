package com.bluebells.examination;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.academics.Term;
import com.bluebells.academics.TermRepository;
import com.bluebells.examination.dto.CreateExamRequest;
import com.bluebells.examination.dto.EnterMarksItemRequest;
import com.bluebells.examination.dto.RecordMarksRequest;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLink;
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
import com.fasterxml.jackson.databind.JsonNode;
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

import java.math.BigDecimal;
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
class ExaminationAndReportCardLifecycleTest {

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
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository linkRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String teacherToken;
    private String parentAToken;

    private AcademicSession currentSession;
    private Term firstTerm;
    private Subject mathSubject;
    private Subject engSubject;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));

        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        User teacher = userRepository.findByUsername("teacher_exam")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("teacher_exam");
                    u.setEmail("teacher.exam@bluebells.edu.pk");
                    u.setFullName("Sir Rashid Exam");
                    u.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(teacherRole));
                    return userRepository.save(u);
                });
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        User userA = userRepository.findByUsername("parent_exam_a")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_exam_a");
                    u.setEmail("parent.exam.a@test.com");
                    u.setFullName("Parent Exam A");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        Guardian guardianA = guardianRepository.findByUserId(userA.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent Exam A", "FATHER", "+92 300 7777771", "41303-7777771-1");
                    g.setUser(userA);
                    return guardianRepository.save(g);
                });

        currentSession = sessionRepository.findByCurrentTrue().orElseThrow();
        firstTerm = termRepository.findAll().get(0);
        Section sectionA = sectionRepository.findAll().get(0);
        mathSubject = subjectRepository.findAll().get(0);
        engSubject = subjectRepository.findAll().get(1);

        student1 = new Student();
        student1.setAdmissionNumber("BSS-EXAM-001");
        student1.setFirstName("Shahid");
        student1.setLastName("Afridi");
        student1.setDateOfBirth(LocalDate.of(2010, 5, 12));
        student1.setGender("MALE");
        student1.setResidentialAddress("Autobahn Road, Hyderabad");
        student1.setAdmissionDate(LocalDate.now());
        student1.setStatus("ACTIVE");
        student1 = studentRepository.save(student1);

        student2 = new Student();
        student2.setAdmissionNumber("BSS-EXAM-002");
        student2.setFirstName("Babar");
        student2.setLastName("Azam");
        student2.setDateOfBirth(LocalDate.of(2010, 8, 20));
        student2.setGender("MALE");
        student2.setResidentialAddress("Latifabad Unit 7, Hyderabad");
        student2.setAdmissionDate(LocalDate.now());
        student2.setStatus("ACTIVE");
        student2 = studentRepository.save(student2);

        enrollmentRepository.save(new Enrollment(student1, currentSession, sectionA, "01", LocalDate.now()));
        enrollmentRepository.save(new Enrollment(student2, currentSession, sectionA, "02", LocalDate.now()));

        linkRepository.save(new GuardianStudentLink(guardianA, student1, true, true));
    }

    @Test
    @DisplayName("Block 7 Verification: Full Exam, Marks Recording, Rank Calculation, and Parent Isolation Lifecycle")
    void testFullExaminationLifecycle() throws Exception {
        // Step 1: Create Exam Schedule
        CreateExamRequest createReq = new CreateExamRequest(
                "Mid-Term Examination 2026-2027",
                currentSession.getId(),
                firstTerm.getId(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(15)
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/exams")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Mid-Term Examination 2026-2027")))
                .andExpect(jsonPath("$.data.status", is("PLANNED")))
                .andReturn();

        JsonNode examJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long examId = examJson.path("data").path("id").asLong();

        // Step 2: Boundary check - Exceeding max marks must be rejected with 400 Bad Request
        EnterMarksItemRequest invalidItem = new EnterMarksItemRequest(student1.getId(), new BigDecimal("105.00"), new BigDecimal("100.00"), false);
        RecordMarksRequest invalidReq = new RecordMarksRequest(examId, mathSubject.getId(), List.of(invalidItem));

        mockMvc.perform(post("/api/v1/exams/" + examId + "/marks")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("cannot exceed maximum marks")));

        // Step 3: Record valid marks for Mathematics
        // Student 1: 90.00 / 100 (90%)
        // Student 2: 70.00 / 100 (70%)
        EnterMarksItemRequest s1Math = new EnterMarksItemRequest(student1.getId(), new BigDecimal("90.00"), new BigDecimal("100.00"), false);
        EnterMarksItemRequest s2Math = new EnterMarksItemRequest(student2.getId(), new BigDecimal("70.00"), new BigDecimal("100.00"), false);
        RecordMarksRequest mathReq = new RecordMarksRequest(examId, mathSubject.getId(), List.of(s1Math, s2Math));

        mockMvc.perform(post("/api/v1/exams/" + examId + "/marks")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mathReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        // Step 4: Record valid marks for English
        // Student 1: 85.00 / 100 (85%) -> Total: 175/200 = 87.5% -> Grade A+
        // Student 2: 60.00 / 100 (60%) -> Total: 130/200 = 65.0% -> Grade B
        EnterMarksItemRequest s1Eng = new EnterMarksItemRequest(student1.getId(), new BigDecimal("85.00"), new BigDecimal("100.00"), false);
        EnterMarksItemRequest s2Eng = new EnterMarksItemRequest(student2.getId(), new BigDecimal("60.00"), new BigDecimal("100.00"), false);
        RecordMarksRequest engReq = new RecordMarksRequest(examId, engSubject.getId(), List.of(s1Eng, s2Eng));

        mockMvc.perform(post("/api/v1/exams/" + examId + "/marks")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(engReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        // Step 5: Publish Results
        mockMvc.perform(post("/api/v1/exams/" + examId + "/publish")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PUBLISHED")));

        // Step 6: Verify Immutability - Post-publish marks entry must be rejected with 409 Conflict
        mockMvc.perform(post("/api/v1/exams/" + examId + "/marks")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mathReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail", containsString("marks entries are locked and immutable")));

        // Step 7: Verify Student 1 Report Card (Total: 175.00/200.00, Pct: 87.50%, Grade: A+, Rank: 1)
        mockMvc.perform(get("/api/v1/exams/" + examId + "/report-cards/student/" + student1.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalMarksObtained", is(175.00)))
                .andExpect(jsonPath("$.data.totalMaxMarks", is(200.00)))
                .andExpect(jsonPath("$.data.overallPercentage", is(87.50)))
                .andExpect(jsonPath("$.data.overallGrade", is("A+")))
                .andExpect(jsonPath("$.data.classRank", is(1)))
                .andExpect(jsonPath("$.data.subjectMarks", hasSize(2)));

        // Step 8: Verify Student 2 Report Card (Total: 130.00/200.00, Pct: 65.00%, Grade: B, Rank: 2)
        mockMvc.perform(get("/api/v1/exams/" + examId + "/report-cards/student/" + student2.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalMarksObtained", is(130.00)))
                .andExpect(jsonPath("$.data.totalMaxMarks", is(200.00)))
                .andExpect(jsonPath("$.data.overallPercentage", is(65.00)))
                .andExpect(jsonPath("$.data.overallGrade", is("B")))
                .andExpect(jsonPath("$.data.classRank", is(2)));

        // Step 9: Verify Row-Level Parent Isolation
        // Parent A is linked to Student 1 -> Can view Student 1 report card
        mockMvc.perform(get("/api/v1/exams/" + examId + "/report-cards/student/" + student1.getId())
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.studentName", is("Shahid Afridi")));

        // Parent A is NOT linked to Student 2 -> Forbidden 403
        mockMvc.perform(get("/api/v1/exams/" + examId + "/report-cards/student/" + student2.getId())
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail", containsString("Access denied")));
    }
}
