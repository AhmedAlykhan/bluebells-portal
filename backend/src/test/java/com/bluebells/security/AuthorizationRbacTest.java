package com.bluebells.security;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
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

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorizationRbacTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String teacherToken;
    private String studentAToken;
    private String studentBToken;

    private Long studentAId;
    private Long studentBId;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Admin")));
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT", "Student")));

        // Admin
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setEmail("admin@test.com");
            u.setFullName("Platform Admin");
            u.setPasswordHash(passwordEncoder.encode("Admin@12345"));
            u.setStatus("ACTIVE");
            u.setRoles(Set.of(adminRole));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        // Teacher
        User teacher = userRepository.findByUsername("teacher_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("teacher_test");
            u.setEmail("teacher.test@test.com");
            u.setFullName("Teacher Faculty");
            u.setPasswordHash(passwordEncoder.encode("Teacher@12345"));
            u.setStatus("ACTIVE");
            u.setRoles(Set.of(teacherRole));
            return userRepository.save(u);
        });
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        // Student A
        User userA = userRepository.findByUsername("student_a_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("student_a_test");
            u.setEmail("student.a@test.com");
            u.setFullName("Student A Khan");
            u.setPasswordHash(passwordEncoder.encode("Student@12345"));
            u.setStatus("ACTIVE");
            u.setRoles(Set.of(studentRole));
            return userRepository.save(u);
        });
        studentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        Student studentA = studentRepository.findByAdmissionNumber("BSS-TEST-0001").map(s -> {
            s.setUser(userA);
            return studentRepository.save(s);
        }).orElseGet(() -> {
            Student s = new Student();
            s.setUser(userA);
            s.setAdmissionNumber("BSS-TEST-0001");
            s.setFirstName("Student");
            s.setLastName("A");
            s.setDateOfBirth(LocalDate.of(2011, 1, 1));
            s.setGender("MALE");
            s.setBloodGroup("B+");
            s.setBFormNumber("41303-0000001-1");
            s.setResidentialAddress("Latifabad Unit 2, Hyderabad");
            s.setAdmissionDate(LocalDate.now());
            s.setStatus("ACTIVE");
            return studentRepository.save(s);
        });
        studentAId = studentA.getId();

        // Student B
        User userB = userRepository.findByUsername("student_b_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("student_b_test");
            u.setEmail("student.b@test.com");
            u.setFullName("Student B Shah");
            u.setPasswordHash(passwordEncoder.encode("Student@12345"));
            u.setStatus("ACTIVE");
            u.setRoles(Set.of(studentRole));
            return userRepository.save(u);
        });
        studentBToken = jwtTokenProvider.generateToken(UserPrincipal.create(userB));

        Student studentB = studentRepository.findByAdmissionNumber("BSS-TEST-0002").map(s -> {
            s.setUser(userB);
            return studentRepository.save(s);
        }).orElseGet(() -> {
            Student s = new Student();
            s.setUser(userB);
            s.setAdmissionNumber("BSS-TEST-0002");
            s.setFirstName("Student");
            s.setLastName("B");
            s.setDateOfBirth(LocalDate.of(2011, 2, 2));
            s.setGender("FEMALE");
            s.setBloodGroup("A+");
            s.setBFormNumber("41303-0000002-2");
            s.setResidentialAddress("Autobahn Road, Hyderabad");
            s.setAdmissionDate(LocalDate.now());
            s.setStatus("ACTIVE");
            return studentRepository.save(s);
        });
        studentBId = studentB.getId();
    }

    @Test
    @DisplayName("Admin user with ROLE_SUPER_ADMIN can access GET /api/v1/users")
    void adminCanAccessUserAdministration() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("Teacher receives 403 Forbidden on GET /api/v1/users")
    void teacherCannotAccessUserAdministration() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Student receives 403 Forbidden on GET /api/v1/users")
    void studentCannotAccessUserAdministration() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Student receives 403 Forbidden when accessing administrative reports dashboard")
    void studentCannotAccessReportsDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/reports/dashboard")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student receives 403 Forbidden when attempting to record fee payments")
    void studentCannotRecordPayment() throws Exception {
        mockMvc.perform(post("/api/v1/finance/payments")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentAId + ",\"amount\":1000,\"paymentMethod\":\"CASH\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("IDOR Protection: Student A cannot access Student B's profile by ID")
    void studentCannotAccessOtherStudentProfile() throws Exception {
        mockMvc.perform(get("/api/v1/students/" + studentBId)
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Student A can access own profile via GET /api/v1/students/me")
    void studentCanAccessOwnProfile() throws Exception {
        mockMvc.perform(get("/api/v1/students/me")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(studentAId.intValue())));
    }

    @Test
    @DisplayName("IDOR Protection: Student A cannot access Student B's attendance history")
    void studentCannotAccessOtherStudentAttendance() throws Exception {
        mockMvc.perform(get("/api/v1/attendance/student/" + studentBId)
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Student A can access own attendance via GET /api/v1/attendance/my")
    void studentCanAccessOwnAttendance() throws Exception {
        mockMvc.perform(get("/api/v1/attendance/my")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("IDOR Protection: Student A cannot access Student B's fee invoices")
    void studentCannotAccessOtherStudentInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/finance/students/" + studentBId + "/invoices")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Teacher receives 403 Forbidden when attempting to view student fee invoices")
    void teacherCannotAccessStudentFeeInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/finance/students/" + studentAId + "/invoices")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Student A can access own fee invoices via GET /api/v1/finance/my-invoices")
    void studentCanAccessOwnInvoices() throws Exception {
        mockMvc.perform(get("/api/v1/finance/my-invoices")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Student A can access own timetable via GET /api/v1/timetable/my")
    void studentCanAccessOwnTimetable() throws Exception {
        mockMvc.perform(get("/api/v1/timetable/my")
                        .header("Authorization", "Bearer " + studentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }
}
