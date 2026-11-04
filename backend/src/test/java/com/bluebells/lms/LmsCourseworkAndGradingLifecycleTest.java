package com.bluebells.lms;

import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLink;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.lms.dto.*;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LmsCourseworkAndGradingLifecycleTest {

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
    private SectionRepository sectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository linkRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String teacherToken;
    private String adminToken;
    private String parentAToken;
    private User teacher;
    private Section sectionA;
    private Subject mathSubject;
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

        teacher = userRepository.findByUsername("teacher_lms_math")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("teacher_lms_math");
                    u.setEmail("lms.math@bluebells.edu.pk");
                    u.setFullName("Sir Rashid LMS Math");
                    u.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(teacherRole));
                    return userRepository.save(u);
                });
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        User userA = userRepository.findByUsername("parent_lms_a")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_lms_a");
                    u.setEmail("parent.lms.a@test.com");
                    u.setFullName("Parent LMS A");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        Guardian guardianA = guardianRepository.findByUserId(userA.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent LMS A", "FATHER", "+92 300 8888881", "41303-8888881-1");
                    g.setUser(userA);
                    return guardianRepository.save(g);
                });

        sectionA = sectionRepository.findAll().get(0);
        mathSubject = subjectRepository.findAll().get(0);

        student1 = new Student();
        student1.setAdmissionNumber("BSS-LMS-001");
        student1.setFirstName("Danish");
        student1.setLastName("Kaneria");
        student1.setDateOfBirth(LocalDate.of(2017, 1, 10));
        student1.setGender("MALE");
        student1.setResidentialAddress("Latifabad Unit 2");
        student1.setAdmissionDate(LocalDate.now());
        student1.setStatus("ACTIVE");
        student1 = studentRepository.save(student1);

        student2 = new Student();
        student2.setAdmissionNumber("BSS-LMS-002");
        student2.setFirstName("Ayesha");
        student2.setLastName("Siddiqua");
        student2.setDateOfBirth(LocalDate.of(2017, 3, 20));
        student2.setGender("FEMALE");
        student2.setResidentialAddress("Autobahn Road");
        student2.setAdmissionDate(LocalDate.now());
        student2.setStatus("ACTIVE");
        student2 = studentRepository.save(student2);

        // Link student 1 to Parent A
        linkRepository.save(new GuardianStudentLink(guardianA, student1, true, true));
    }

    @Test
    @DisplayName("Complete LMS Lifecycle: Course -> Lessons -> Assignment -> Submission -> Teacher Grading with Boundary Validation")
    void testLmsLifecycleAndGrading() throws Exception {
        // 1. Teacher creates course
        CreateCourseRequest courseReq = new CreateCourseRequest();
        courseReq.setSubjectId(mathSubject.getId());
        courseReq.setSectionId(sectionA.getId());
        courseReq.setTeacherId(teacher.getId());
        courseReq.setTitle("Mathematics Grade 9 - Session 2026-2027");
        courseReq.setDescription("Comprehensive SSC Part 1 Mathematics Curriculum");

        MvcResult courseResult = mockMvc.perform(post("/api/v1/lms/courses")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Mathematics Grade 9 - Session 2026-2027")))
                .andReturn();

        Long courseId = objectMapper.readTree(courseResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 2. Add Lessons in Sequence Order
        CreateLessonRequest lesson1 = new CreateLessonRequest();
        lesson1.setTitle("Chapter 1: Matrices & Determinants");
        lesson1.setContent("Definition of matrices, types, row and column operations.");
        lesson1.setSequenceOrder(1);

        mockMvc.perform(post("/api/v1/lms/courses/" + courseId + "/lessons")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lesson1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("Chapter 1: Matrices & Determinants")));

        CreateLessonRequest lesson2 = new CreateLessonRequest();
        lesson2.setTitle("Chapter 2: Real & Complex Numbers");
        lesson2.setContent("Properties of real numbers, radicals and radices.");
        lesson2.setSequenceOrder(2);

        mockMvc.perform(post("/api/v1/lms/courses/" + courseId + "/lessons")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lesson2)))
                .andExpect(status().isCreated());

        // 3. Verify lessons returned in order
        mockMvc.perform(get("/api/v1/lms/courses/" + courseId + "/lessons")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].sequenceOrder", is(1)))
                .andExpect(jsonPath("$.data[1].sequenceOrder", is(2)));

        // 4. Create an expired assignment with allowLateSubmission = false
        CreateAssignmentRequest expiredReq = new CreateAssignmentRequest();
        expiredReq.setTitle("Quiz 1: Quick Matrix Multiplication");
        expiredReq.setInstructions("Solve in 10 minutes");
        expiredReq.setMaxMarks(new BigDecimal("20.00"));
        expiredReq.setDueDate(Instant.now().minus(2, ChronoUnit.DAYS)); // Past due date
        expiredReq.setAllowLateSubmission(false);

        MvcResult expiredResult = mockMvc.perform(post("/api/v1/lms/courses/" + courseId + "/assignments")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expiredReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long expiredAssId = objectMapper.readTree(expiredResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 5. Attempting to submit past-due assignment without late submission allowed MUST be rejected with 400
        SubmitAssignmentRequest lateSubmitReq = new SubmitAssignmentRequest(
                student1.getId(), "Late submission attempt", null);

        mockMvc.perform(post("/api/v1/lms/assignments/" + expiredAssId + "/submit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lateSubmitReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Late submissions are not accepted")));

        // 6. Create active assignment with due date +5 days
        CreateAssignmentRequest activeAssReq = new CreateAssignmentRequest();
        activeAssReq.setTitle("Homework 1: Inversion & Cramer's Rule");
        activeAssReq.setInstructions("Solve Exercise 1.6 questions 1 through 5 step-by-step.");
        activeAssReq.setMaxMarks(new BigDecimal("100.00"));
        activeAssReq.setDueDate(Instant.now().plus(5, ChronoUnit.DAYS));
        activeAssReq.setAllowLateSubmission(false);

        MvcResult activeAssResult = mockMvc.perform(post("/api/v1/lms/courses/" + courseId + "/assignments")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activeAssReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long activeAssId = objectMapper.readTree(activeAssResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 7. Student 1 submits assignment
        SubmitAssignmentRequest validSubmitReq = new SubmitAssignmentRequest(
                student1.getId(),
                "Solved all 5 questions using Cramer's rule determinants.",
                "https://storage.bluebells.edu.pk/submissions/bss-lms-001/hw1.pdf"
        );

        MvcResult subResult = mockMvc.perform(post("/api/v1/lms/assignments/" + activeAssId + "/submit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSubmitReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("SUBMITTED")))
                .andExpect(jsonPath("$.data.studentName", is("Danish Kaneria")))
                .andReturn();

        Long submissionId = objectMapper.readTree(subResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 8. Teacher grading: marks exceeding maxMarks (105.00 > 100.00) MUST be rejected with 400 Bad Request
        GradeSubmissionRequest invalidGradeReq = new GradeSubmissionRequest(
                new BigDecimal("105.00"), "Bonus marks beyond max"
        );

        mockMvc.perform(post("/api/v1/lms/submissions/" + submissionId + "/grade")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidGradeReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("cannot exceed maximum marks")));

        // 9. Teacher grades with valid marks (94.50 / 100.00)
        GradeSubmissionRequest validGradeReq = new GradeSubmissionRequest(
                new BigDecimal("94.50"), "Clean working. Minor calculation slip in question 3."
        );

        mockMvc.perform(post("/api/v1/lms/submissions/" + submissionId + "/grade")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validGradeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("GRADED")))
                .andExpect(jsonPath("$.data.marksObtained", is(94.50)))
                .andExpect(jsonPath("$.data.teacherFeedback", containsString("Minor calculation slip")));

        // 10. Parent A can view their child Student 1's submission and grade
        mockMvc.perform(get("/api/v1/lms/assignments/" + activeAssId + "/submissions/student/" + student1.getId())
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.marksObtained", is(94.50)));

        // 11. Parent A attempting to view Student 2's submission MUST receive 403 Forbidden
        mockMvc.perform(get("/api/v1/lms/assignments/" + activeAssId + "/submissions/student/" + student2.getId())
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isForbidden());
    }
}
