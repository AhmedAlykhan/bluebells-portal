package com.bluebells.analytics;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ManagementAnalyticsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT", "Student")));

        User admin = userRepository.findByUsername("analytics_admin_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("analytics_admin_test");
            u.setEmail("analytics_admin@bluebells.test");
            u.setFullName("Analytics Admin");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        User student = userRepository.findByUsername("analytics_student_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("analytics_student_test");
            u.setEmail("analytics_student@bluebells.test");
            u.setFullName("Analytics Student");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(studentRole)));
            return userRepository.save(u);
        });
        studentToken = jwtTokenProvider.generateToken(UserPrincipal.create(student));
    }

    @Test
    @DisplayName("Admin can fetch management analytics dashboard data successfully")
    void testGetManagementAnalyticsSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/overview")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studentAnalytics").exists())
                .andExpect(jsonPath("$.data.attendanceAnalytics").exists())
                .andExpect(jsonPath("$.data.academicAnalytics").exists())
                .andExpect(jsonPath("$.data.financialAnalytics").exists())
                .andExpect(jsonPath("$.data.lmsAnalytics").exists())
                .andExpect(jsonPath("$.data.staffAnalytics").exists());
    }

    @Test
    @DisplayName("Admin can fetch management analytics with date and session filters")
    void testGetManagementAnalyticsWithFilters() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/overview")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studentAnalytics.totalActiveStudents").isNumber())
                .andExpect(jsonPath("$.data.financialAnalytics.totalBilled").isNumber());
    }

    @Test
    @DisplayName("Admin can export analytics Excel report")
    void testExportAnalyticsExcel() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/export/excel")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("spreadsheetml")))
                .andExpect(header().string("Content-Disposition", containsString("bluebells_school_analytics.xlsx")));
    }

    @Test
    @DisplayName("Student is forbidden from viewing management analytics (RBAC check)")
    void testStudentForbiddenFromAnalytics() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/overview")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
