package com.bluebells.dataimport;

import com.bluebells.identity.Permission;
import com.bluebells.identity.PermissionRepository;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
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

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DataImportAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String superAdminToken;
    private String principalToken;
    private String teacherToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));
        Role principalRole = roleRepository.findByName("ROLE_PRINCIPAL")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PRINCIPAL", "Principal")));
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER", "Teacher")));
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT", "Student")));

        // Super Admin
        User superAdmin = userRepository.findByUsername("superadmin_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("superadmin_test");
            u.setEmail("superadmin@test.com");
            u.setFullName("Super Administrator");
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        superAdminToken = jwtTokenProvider.generateToken(UserPrincipal.create(superAdmin));

        // Principal with DATA_IMPORT_VIEW permission
        Permission viewPerm = permissionRepository.findByCode("DATA_IMPORT_VIEW")
                .orElseGet(() -> permissionRepository.save(new Permission("DATA_IMPORT_VIEW", "DATA_IMPORT", "View import")));

        User principal = userRepository.findByUsername("principal_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("principal_test");
            u.setEmail("principal@test.com");
            u.setFullName("Huma Arif Principal");
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(principalRole)));
            u.setDirectPermissions(new HashSet<>(Set.of(viewPerm)));
            return userRepository.save(u);
        });
        principalToken = jwtTokenProvider.generateToken(UserPrincipal.create(principal));

        // Teacher
        User teacher = userRepository.findByUsername("teacher_test_import").orElseGet(() -> {
            User u = new User();
            u.setUsername("teacher_test_import");
            u.setEmail("teacher.import@test.com");
            u.setFullName("Sir Ahmed Khan");
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(teacherRole)));
            return userRepository.save(u);
        });
        teacherToken = jwtTokenProvider.generateToken(UserPrincipal.create(teacher));

        // Student
        User student = userRepository.findByUsername("student_test_import").orElseGet(() -> {
            User u = new User();
            u.setUsername("student_test_import");
            u.setEmail("student.import@test.com");
            u.setFullName("Ali Khan Student");
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(studentRole)));
            return userRepository.save(u);
        });
        studentToken = jwtTokenProvider.generateToken(UserPrincipal.create(student));
    }

    @Test
    @DisplayName("Unauthenticated request to bulk data import endpoints returns 401 Unauthorized")
    void testUnauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/data-import/statistics"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/admin/data-import/history"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Teacher access to bulk data import endpoints returns 403 Forbidden")
    void testTeacherAccessReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/data-import/statistics")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Student access to bulk data import endpoints returns 403 Forbidden")
    void testStudentAccessReturns403() throws Exception {
        mockMvc.perform(get("/api/v1/admin/data-import/statistics")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Super Admin has full access to bulk data import statistics and permissions")
    void testSuperAdminAccessSucceeds() throws Exception {
        mockMvc.perform(get("/api/v1/admin/data-import/statistics")
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/v1/admin/data-import/permissions")
                        .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Principal with DATA_IMPORT_VIEW can view statistics but cannot access Super Admin permission management")
    void testPrincipalAccessControls() throws Exception {
        // Can view statistics
        mockMvc.perform(get("/api/v1/admin/data-import/statistics")
                        .header("Authorization", "Bearer " + principalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Cannot manage permissions (requires ROLE_SUPER_ADMIN)
        mockMvc.perform(get("/api/v1/admin/data-import/permissions")
                        .header("Authorization", "Bearer " + principalToken))
                .andExpect(status().isForbidden());
    }
}
