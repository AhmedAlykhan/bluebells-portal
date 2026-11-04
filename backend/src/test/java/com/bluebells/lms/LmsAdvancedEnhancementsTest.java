package com.bluebells.lms;

import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.academics.Subject;
import com.bluebells.academics.SubjectRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LmsAdvancedEnhancementsTest {

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
    private CourseRepository courseRepository;

    @Autowired
    private CourseModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    @Autowired
    private SubmissionFileRepository submissionFileRepository;

    @Autowired
    private LessonProgressRepository progressRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String teacherToken;
    private String adminToken;
    private String studentToken;
    private User teacher;
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT", "Student")));

        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        teacher = new User();
        teacher.setUsername("test_teacher_" + System.currentTimeMillis());
        teacher.setEmail("teacher" + System.currentTimeMillis() + "@bluebells.edu.pk");
        teacher.setFullName("Sir Test Instructor");
        teacher.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
        teacher.setStatus("ACTIVE");
        teacher.setRoles(Set.of(teacherRole));
        teacher = userRepository.save(teacher);
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        User studentUser = new User();
        studentUser.setUsername("test_student_" + System.currentTimeMillis());
        studentUser.setEmail("student" + System.currentTimeMillis() + "@bluebells.edu.pk");
        studentUser.setFullName("Student Test User");
        studentUser.setPasswordHash(passwordEncoder.encode("Student@12345"));
        studentUser.setStatus("ACTIVE");
        studentUser.setRoles(Set.of(studentRole));
        studentUser = userRepository.save(studentUser);
        studentToken = jwtTokenProvider.generateToken(UserPrincipal.create(studentUser));

        student = new Student();
        student.setUser(studentUser);
        student.setAdmissionNumber("BSS-TEST-" + System.currentTimeMillis());
        student.setFirstName("Student");
        student.setLastName("Test User");
        student.setDateOfBirth(java.time.LocalDate.of(2010, 1, 1));
        student.setGender("MALE");
        student.setResidentialAddress("Latifabad Unit 2, Hyderabad");
        student.setAdmissionDate(java.time.LocalDate.now());
        student.setStatus("ACTIVE");
        student = studentRepository.save(student);

        Section section = sectionRepository.findAll().stream().findFirst().orElseThrow();
        Subject subject = subjectRepository.findAll().stream().findFirst().orElseThrow();

        course = new Course(subject, section, teacher, "Advanced Matric Mathematics", "Curriculum for Class 9 Matriculation");
        course = courseRepository.save(course);
    }

    @Test
    @DisplayName("Module and YouTube Video lifecycle: create module, integrate video with URL parsing")
    void testModuleAndYouTubeVideoLifecycle() throws Exception {
        // 1. Create Module
        CreateModuleRequest modReq = new CreateModuleRequest("Unit 1: Real and Complex Numbers", "Detailed study", 1);
        MvcResult modRes = mockMvc.perform(post("/api/v1/lms/courses/{courseId}/modules", course.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("Unit 1: Real and Complex Numbers")))
                .andReturn();

        CourseModuleDto modDto = objectMapper.readValue(
                objectMapper.readTree(modRes.getResponse().getContentAsString()).get("data").toString(),
                CourseModuleDto.class
        );
        assertNotNull(modDto.getId());

        // 2. Create Lesson in Module
        CreateLessonRequest lessonReq = new CreateLessonRequest(
                modDto.getId(),
                "Conjugates and Real Numbers",
                "Study of imaginary unit i",
                1,
                "LECTURE",
                45
        );
        mockMvc.perform(post("/api/v1/lms/courses/{courseId}/lessons", course.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("Conjugates and Real Numbers")))
                .andExpect(jsonPath("$.data.durationMinutes", is(45)));

        // 3. Integrate YouTube video (testing URL parser)
        CreateYouTubeVideoRequest ytReq = new CreateYouTubeVideoRequest();
        ytReq.setModuleId(modDto.getId());
        ytReq.setTitle("Complex Numbers Explained");
        ytReq.setYoutubeUrl("https://www.youtube.com/watch?v=5PcpBw5HbWo");
        ytReq.setDurationSeconds(840);
        ytReq.setDisplayOrder(1);

        mockMvc.perform(post("/api/v1/lms/courses/{courseId}/videos", course.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ytReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.videoId", is("5PcpBw5HbWo")))
                .andExpect(jsonPath("$.data.durationSeconds", is(840)));

        // Verify list of videos
        mockMvc.perform(get("/api/v1/lms/courses/{courseId}/videos", course.getId())
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].videoId", is("5PcpBw5HbWo")));
    }

    @Test
    @DisplayName("Multi-photo homework submission and teacher grading with return-for-correction workflow")
    void testMultiPhotoSubmissionAndGradingLifecycle() throws Exception {
        // 1. Teacher publishes assignment with allowedFormats & passingMarks
        CreateAssignmentRequest assignReq = new CreateAssignmentRequest();
        assignReq.setTitle("Exercise 1.2 Handwritten Solutions");
        assignReq.setInstructions("Solve all questions on notebook and upload photos");
        assignReq.setMaxMarks(new BigDecimal("50.00"));
        assignReq.setPassingMarks(new BigDecimal("20.00"));
        assignReq.setDueDate(Instant.now().plus(7, ChronoUnit.DAYS));
        assignReq.setAllowLateSubmission(true);
        assignReq.setAllowedFormats("IMAGES,PDF");

        MvcResult assignRes = mockMvc.perform(post("/api/v1/lms/courses/{courseId}/assignments", course.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignReq)))
                .andExpect(status().isCreated())
                .andReturn();

        AssignmentDto assignDto = objectMapper.readValue(
                objectMapper.readTree(assignRes.getResponse().getContentAsString()).get("data").toString(),
                AssignmentDto.class
        );

        // 2. Student submits multi-photo homework
        SubmitAssignmentRequest submitReq = new SubmitAssignmentRequest();
        submitReq.setStudentId(student.getId());
        submitReq.setSubmissionText("Sir, here are pages 1 and 2 of my handwritten homework.");
        submitReq.setFileUrl("/storage/uploads/submissions/test_page1.jpg");
        submitReq.setFileUrls(List.of(
                "/storage/uploads/submissions/test_page1.jpg",
                "/storage/uploads/submissions/test_page2.jpg"
        ));
        submitReq.setFileNames(List.of("Homework_Page_1.jpg", "Homework_Page_2.jpg"));
        submitReq.setFileSizes(List.of(1200000L, 1150000L));
        submitReq.setFileTypes(List.of("IMAGE", "IMAGE"));

        MvcResult subRes = mockMvc.perform(post("/api/v1/lms/assignments/{assignmentId}/submit", assignDto.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("SUBMITTED")))
                .andReturn();

        AssignmentSubmissionDto subDto = objectMapper.readValue(
                objectMapper.readTree(subRes.getResponse().getContentAsString()).get("data").toString(),
                AssignmentSubmissionDto.class
        );

        // 3. Teacher returns for correction
        GradeSubmissionRequest correctionReq = new GradeSubmissionRequest(
                new BigDecimal("0.00"),
                "Page 2 is blurry. Please re-photograph with better lighting and resubmit.",
                "RETURNED_FOR_CORRECTION"
        );

        mockMvc.perform(post("/api/v1/lms/submissions/{submissionId}/grade", subDto.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctionReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("RETURNED_FOR_CORRECTION")))
                .andExpect(jsonPath("$.data.teacherFeedback", containsString("blurry")));

        // 4. Student resubmits corrected pages
        submitReq.setSubmissionText("Re-uploaded clear photo for Page 2.");
        mockMvc.perform(post("/api/v1/lms/assignments/{assignmentId}/submit", assignDto.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("RESUBMITTED")));

        // 5. Teacher awards final grade
        GradeSubmissionRequest finalGradeReq = new GradeSubmissionRequest(
                new BigDecimal("48.50"),
                "Excellent work! All steps clearly demonstrated.",
                "GRADED"
        );

        mockMvc.perform(post("/api/v1/lms/submissions/{submissionId}/grade", subDto.getId())
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finalGradeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("GRADED")))
                .andExpect(jsonPath("$.data.marksObtained", is(48.50)))
                .andExpect(jsonPath("$.data.passed", is(true)));
    }

    @Test
    @DisplayName("Lesson progress tracking: mark lesson complete and get course percentage")
    void testLessonProgressTracking() throws Exception {
        // Create 2 lessons
        Lesson l1 = lessonRepository.save(new Lesson(course, "Lesson 1", "Content 1", 1));
        Lesson l2 = lessonRepository.save(new Lesson(course, "Lesson 2", "Content 2", 2));

        // Mark Lesson 1 complete
        MarkLessonProgressRequest progReq = new MarkLessonProgressRequest(student.getId(), true);
        mockMvc.perform(post("/api/v1/lms/lessons/{lessonId}/progress", l1.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed", is(true)));

        // Check course progress summary (1 of 2 completed = 50.0%)
        mockMvc.perform(get("/api/v1/lms/courses/{courseId}/progress/student/{studentId}", course.getId(), student.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalLessons", is(2)))
                .andExpect(jsonPath("$.data.completedLessons", is(1)))
                .andExpect(jsonPath("$.data.completionPercentage", is(50.0)));
    }
}
