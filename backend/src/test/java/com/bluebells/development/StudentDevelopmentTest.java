package com.bluebells.development;

import com.bluebells.development.dto.CreateDevelopmentRecordRequest;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentDevelopmentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository guardianStudentLinkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String teacherToken;
    private String parentToken;
    private String strangerParentToken;
    private Long linkedStudentId;
    private Long strangerStudentId;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));

        User admin = userRepository.findByUsername("dev_admin_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("dev_admin_test");
            u.setEmail("dev_admin@bluebells.test");
            u.setFullName("Principal Huma Arif");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        User teacher = userRepository.findByUsername("dev_teacher_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("dev_teacher_test");
            u.setEmail("dev_teacher@bluebells.test");
            u.setFullName("Sir Ahmed Khan Teacher");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(teacherRole)));
            return userRepository.save(u);
        });
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        User parentUser = userRepository.findByUsername("dev_parent_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("dev_parent_test");
            u.setEmail("dev_parent@bluebells.test");
            u.setFullName("Tariq Khan Parent");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(parentRole)));
            return userRepository.save(u);
        });
        parentToken = jwtTokenProvider.generateToken(UserPrincipal.create(parentUser));

        User strangerParent = userRepository.findByUsername("dev_stranger_parent").orElseGet(() -> {
            User u = new User();
            u.setUsername("dev_stranger_parent");
            u.setEmail("stranger_parent@bluebells.test");
            u.setFullName("Stranger Parent");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(parentRole)));
            return userRepository.save(u);
        });
        strangerParentToken = jwtTokenProvider.generateToken(UserPrincipal.create(strangerParent));

        Guardian guardian = guardianRepository.findByUserId(parentUser.getId()).orElseGet(() -> {
            Guardian g = new Guardian("Tariq Khan", "FATHER", "+92 300 1234567", "41303-1111111-1");
            g.setUser(parentUser);
            return guardianRepository.save(g);
        });

        Student student1 = studentRepository.findByAdmissionNumber("BSS-DEV-001").orElseGet(() -> {
            Student s = new Student();
            s.setAdmissionNumber("BSS-DEV-001");
            s.setFirstName("Ali");
            s.setLastName("Khan");
            s.setDateOfBirth(LocalDate.of(2012, 5, 14));
            s.setGender("MALE");
            s.setResidentialAddress("Unit 2 Latifabad Hyderabad");
            s.setAdmissionDate(LocalDate.of(2024, 8, 1));
            s.setStatus("ACTIVE");
            return studentRepository.save(s);
        });
        linkedStudentId = student1.getId();

        Student student2 = studentRepository.findByAdmissionNumber("BSS-DEV-002").orElseGet(() -> {
            Student s = new Student();
            s.setAdmissionNumber("BSS-DEV-002");
            s.setFirstName("Bilal");
            s.setLastName("Ahmed");
            s.setDateOfBirth(LocalDate.of(2013, 3, 20));
            s.setGender("MALE");
            s.setResidentialAddress("Autobahn Road Hyderabad");
            s.setAdmissionDate(LocalDate.of(2024, 8, 1));
            s.setStatus("ACTIVE");
            return studentRepository.save(s);
        });
        strangerStudentId = student2.getId();

        if (!guardianStudentLinkRepository.existsByGuardianUserIdAndStudentId(parentUser.getId(), linkedStudentId)) {
            GuardianStudentLink link = new GuardianStudentLink(guardian, student1, true, true);
            guardianStudentLinkRepository.save(link);
        }
    }

    @Test
    @DisplayName("Teacher can create student development achievement record")
    void testTeacherCanCreateAchievement() throws Exception {
        CreateDevelopmentRecordRequest req = new CreateDevelopmentRecordRequest();
        req.setStudentId(linkedStudentId);
        req.setCategory("SPORTS");
        req.setTitle("Inter-School Football Championship Gold Medal");
        req.setDescription("Captained the senior football team to a 3-1 victory in the Hyderabad Inter-School League.");
        req.setAchievementDate(LocalDate.of(2026, 2, 10));
        req.setPositionOrAward("Gold Medalist");
        req.setLevel("INTER_SCHOOL");
        req.setSkillsAcquired("Team Leadership, Athletic Endurance, Strategic Play");

        mockMvc.perform(post("/api/v1/development/records")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.title").value("Inter-School Football Championship Gold Medal"))
                .andExpect(jsonPath("$.data.category").value("SPORTS"))
                .andExpect(jsonPath("$.data.skillsAcquired").value(containsString("Leadership")));
    }

    @Test
    @DisplayName("Parent can view development portfolio of their linked child")
    void testParentCanViewLinkedChildDevelopment() throws Exception {
        mockMvc.perform(get("/api/v1/development/students/" + linkedStudentId)
                        .header("Authorization", "Bearer " + parentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Parent CANNOT view development portfolio of unlinked student (403 Forbidden)")
    void testParentCannotViewUnlinkedStudent() throws Exception {
        mockMvc.perform(get("/api/v1/development/students/" + strangerStudentId)
                        .header("Authorization", "Bearer " + strangerParentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Leadership can officially verify a pending student development achievement")
    void testLeadershipCanVerifyAchievement() throws Exception {
        CreateDevelopmentRecordRequest req = new CreateDevelopmentRecordRequest();
        req.setStudentId(linkedStudentId);
        req.setCategory("COMPETITION");
        req.setTitle("Sindh Science Fair First Prize");
        req.setDescription("Robotics innovation project model.");
        req.setAchievementDate(LocalDate.of(2026, 3, 5));
        req.setPositionOrAward("1st Position");
        req.setLevel("PROVINCIAL");
        req.setSkillsAcquired("Robotics, Circuit Design");

        org.springframework.test.web.servlet.MvcResult res = mockMvc.perform(post("/api/v1/development/records")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING_REVIEW"))
                .andReturn();

        long recId = objectMapper.readTree(res.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Admin verifies record
        mockMvc.perform(patch("/api/v1/development/records/" + recId + "/verify")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("VERIFIED"));
    }
}
