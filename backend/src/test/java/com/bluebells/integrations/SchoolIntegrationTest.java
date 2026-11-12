package com.bluebells.integrations;

import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.integrations.dto.UpdateIntegrationRequest;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
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

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SchoolIntegrationTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SchoolIntegrationRepository integrationRepository;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT", "Student")));

        User admin = userRepository.findByUsername("integration_admin_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("integration_admin_test");
            u.setEmail("integ_admin@bluebells.test");
            u.setFullName("Integration Admin");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        User student = userRepository.findByUsername("integration_student_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("integration_student_test");
            u.setEmail("integ_student@bluebells.test");
            u.setFullName("Integration Student");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(studentRole)));
            return userRepository.save(u);
        });
        studentToken = jwtTokenProvider.generateToken(UserPrincipal.create(student));

        // Ensure default connectors exist
        if (integrationRepository.findByIntegrationKey("SMS_GATEWAY").isEmpty()) {
            SchoolIntegration s = new SchoolIntegration();
            s.setIntegrationKey("SMS_GATEWAY");
            s.setName("SMS Notification Gateway");
            s.setCategory("COMMUNICATION");
            s.setDescription("Transactional and emergency broadcast SMS engine");
            s.setStatus("OPERATIONAL_SIMULATED");
            s.setEnabled(true);
            integrationRepository.save(s);
        }

        if (integrationRepository.findByIntegrationKey("EXTERNAL_CALENDAR").isEmpty()) {
            SchoolIntegration s = new SchoolIntegration();
            s.setIntegrationKey("EXTERNAL_CALENDAR");
            s.setName("Academic Calendar Sync");
            s.setCategory("CALENDAR");
            s.setDescription("iCalendar and Google Calendar synchronization feed");
            s.setStatus("CONFIGURED_OPERATIONAL");
            s.setEnabled(true);
            integrationRepository.save(s);
        }
    }

    @Test
    @DisplayName("Admin can fetch all external integration connectors")
    void testGetAllIntegrations() throws Exception {
        mockMvc.perform(get("/api/v1/integrations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data[0].integrationKey").isString())
                .andExpect(jsonPath("$.data[0].name").isString());
    }

    @Test
    @DisplayName("Security: Secrets and passwords in config_values are masked and NOT exposed in API responses")
    void testSecretsNotExposedInDto() throws Exception {
        // Update connector with secret
        UpdateIntegrationRequest req = new UpdateIntegrationRequest();
        req.setEnabled(true);
        req.setStatus("OPERATIONAL_SIMULATED");
        req.setConfigValues("{\"apiKey\":\"TOP_SECRET_SUPER_KEY_9999\",\"apiSecret\":\"VERY_CONFIDENTIAL_123\"}");

        mockMvc.perform(put("/api/v1/integrations/SMS_GATEWAY")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Fetch connector and assert raw secrets are NOT in response
        MvcResult result = mockMvc.perform(get("/api/v1/integrations/SMS_GATEWAY")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(content.contains("TOP_SECRET_SUPER_KEY_9999"), "Raw API key must never be exposed in integration DTO");
        assertFalse(content.contains("VERY_CONFIDENTIAL_123"), "Raw API secret must never be exposed in integration DTO");
    }

    @Test
    @DisplayName("Admin can ping / test integration connectivity and heartbeat latency")
    void testPingIntegrationHeartbeat() throws Exception {
        mockMvc.perform(post("/api/v1/integrations/EXTERNAL_CALENDAR/ping")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.integrationKey").value("EXTERNAL_CALENDAR"))
                .andExpect(jsonPath("$.data.timestamp").exists());
    }

    @Test
    @DisplayName("RBAC: Student is strictly forbidden from accessing integrations console (403 Forbidden)")
    void testStudentForbiddenFromIntegrations() throws Exception {
        mockMvc.perform(get("/api/v1/integrations")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
