package com.bluebells.guardians;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GuardianMultiChildIsolationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository linkRepository;

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

    private String parentAToken;
    private String parentBToken;
    private Long student1Id;
    private Long student2Id;
    private Long student3Id;

    @BeforeEach
    void setUp() {
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent/Guardian")));

        // Parent A
        User userA = userRepository.findByUsername("parent_a_test")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_a_test");
                    u.setEmail("parent.a@test.com");
                    u.setFullName("Parent A");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        Guardian guardianA = guardianRepository.findByUserId(userA.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent A", "FATHER", "+92 300 1111111", "41303-1111111-1");
                    g.setUser(userA);
                    return guardianRepository.save(g);
                });

        // Parent B
        User userB = userRepository.findByUsername("parent_b_test")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_b_test");
                    u.setEmail("parent.b@test.com");
                    u.setFullName("Parent B");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentBToken = jwtTokenProvider.generateToken(UserPrincipal.create(userB));

        Guardian guardianB = guardianRepository.findByUserId(userB.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent B", "MOTHER", "+92 300 2222222", "41303-2222222-2");
                    g.setUser(userB);
                    return guardianRepository.save(g);
                });

        Section section = sectionRepository.findAll().get(0);
        AcademicSession session = sessionRepository.findByIsCurrentTrue().orElseGet(() -> sessionRepository.findAll().get(0));

        // Student 1 (Parent A's child)
        Student s1 = studentRepository.findByAdmissionNumber("BSS-TEST-0001")
                .orElseGet(() -> {
                    Student s = new Student();
                    s.setAdmissionNumber("BSS-TEST-0001");
                    s.setFirstName("Zain");
                    s.setLastName("Ahmad");
                    s.setDateOfBirth(LocalDate.of(2017, 3, 10));
                    s.setGender("MALE");
                    s.setResidentialAddress("Latifabad Unit 2");
                    s.setAdmissionDate(LocalDate.now());
                    s.setStatus("ACTIVE");
                    Student saved = studentRepository.save(s);
                    Enrollment e = new Enrollment(saved, session, section, "101", LocalDate.now());
                    enrollmentRepository.save(e);
                    saved.getEnrollments().add(e);
                    return saved;
                });
        student1Id = s1.getId();

        // Student 2 (Parent B's child)
        Student s2 = studentRepository.findByAdmissionNumber("BSS-TEST-0002")
                .orElseGet(() -> {
                    Student s = new Student();
                    s.setAdmissionNumber("BSS-TEST-0002");
                    s.setFirstName("Sara");
                    s.setLastName("Khan");
                    s.setDateOfBirth(LocalDate.of(2016, 7, 22));
                    s.setGender("FEMALE");
                    s.setResidentialAddress("Autobahn Road, Hyderabad");
                    s.setAdmissionDate(LocalDate.now());
                    s.setStatus("ACTIVE");
                    Student saved = studentRepository.save(s);
                    Enrollment e = new Enrollment(saved, session, section, "102", LocalDate.now());
                    enrollmentRepository.save(e);
                    saved.getEnrollments().add(e);
                    return saved;
                });
        student2Id = s2.getId();

        // Student 3 (Parent A's second child - multi-child support)
        Student s3 = studentRepository.findByAdmissionNumber("BSS-TEST-0003")
                .orElseGet(() -> {
                    Student s = new Student();
                    s.setAdmissionNumber("BSS-TEST-0003");
                    s.setFirstName("Amina");
                    s.setLastName("Ahmad");
                    s.setDateOfBirth(LocalDate.of(2019, 11, 5));
                    s.setGender("FEMALE");
                    s.setResidentialAddress("Latifabad Unit 2");
                    s.setAdmissionDate(LocalDate.now());
                    s.setStatus("ACTIVE");
                    Student saved = studentRepository.save(s);
                    Enrollment e = new Enrollment(saved, session, section, "103", LocalDate.now());
                    enrollmentRepository.save(e);
                    saved.getEnrollments().add(e);
                    return saved;
                });
        student3Id = s3.getId();

        // Link student 1 to Parent A
        if (!linkRepository.existsById(new GuardianStudentLinkId(guardianA.getId(), s1.getId()))) {
            linkRepository.save(new GuardianStudentLink(guardianA, s1, true, true));
        }

        // Link student 3 to Parent A (Parent A has multiple children)
        if (!linkRepository.existsById(new GuardianStudentLinkId(guardianA.getId(), s3.getId()))) {
            linkRepository.save(new GuardianStudentLink(guardianA, s3, true, true));
        }

        // Link student 2 to Parent B
        if (!linkRepository.existsById(new GuardianStudentLinkId(guardianB.getId(), s2.getId()))) {
            linkRepository.save(new GuardianStudentLink(guardianB, s2, true, true));
        }
    }

    @Test
    @DisplayName("Parent A retrieves their own children (multi-child) and cannot view Parent B's child")
    void testParentMultiChildIsolation() throws Exception {
        // 1. Parent A accesses /api/v1/guardians/my-children -> should receive Zain and Amina, NOT Sara
        mockMvc.perform(get("/api/v1/guardians/my-children")
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].firstName", containsInAnyOrder("Zain", "Amina")))
                .andExpect(jsonPath("$.data[*].firstName", not(hasItem("Sara"))));

        // 2. Parent A can view their child Student 1
        mockMvc.perform(get("/api/v1/students/" + student1Id)
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName", is("Zain")));

        // 3. Parent A can view their child Student 3
        mockMvc.perform(get("/api/v1/students/" + student3Id)
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName", is("Amina")));

        // 4. Parent A attempting to view Parent B's child Student 2 MUST receive 403 Forbidden
        mockMvc.perform(get("/api/v1/students/" + student2Id)
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isForbidden());

        // 5. Parent B accessing /api/v1/guardians/my-children -> should receive Sara ONLY
        mockMvc.perform(get("/api/v1/guardians/my-children")
                        .header("Authorization", "Bearer " + parentBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].firstName", is("Sara")));

        // 6. Parent B attempting to view Parent A's child Student 1 MUST receive 403 Forbidden
        mockMvc.perform(get("/api/v1/students/" + student1Id)
                        .header("Authorization", "Bearer " + parentBToken))
                .andExpect(status().isForbidden());
    }
}
