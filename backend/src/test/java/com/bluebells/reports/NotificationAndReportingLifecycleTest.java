package com.bluebells.reports;

import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.notifications.dto.SendNotificationRequest;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
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

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationAndReportingLifecycleTest {

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
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userAToken;
    private String userBToken;
    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));

        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        userA = userRepository.findByUsername("user_rep_a")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("user_rep_a");
                    u.setEmail("user.rep.a@bluebells.edu.pk");
                    u.setFullName("User Reports A");
                    u.setPasswordHash(passwordEncoder.encode("Pass@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        userAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        userB = userRepository.findByUsername("user_rep_b")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("user_rep_b");
                    u.setEmail("user.rep.b@bluebells.edu.pk");
                    u.setFullName("User Reports B");
                    u.setPasswordHash(passwordEncoder.encode("Pass@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        userBToken = jwtTokenProvider.generateToken(UserPrincipal.create(userB));
    }

    @Test
    @DisplayName("Block 9 Verification: Notification Dispatch, Read Tracking, Security Isolation, and CSV Injection Guards")
    void testNotificationAndReportingLifecycle() throws Exception {
        // Step 1: Admin sends notification to User A
        SendNotificationRequest sendReq = new SendNotificationRequest(
                userA.getId(),
                null,
                "Fee Due Reminder",
                "Dear Parent, monthly fee for November is due on 15th Nov.",
                "FINANCE"
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", is("Fee Due Reminder")));

        // Step 2: User A checks unread count -> 1
        mockMvc.perform(get("/api/v1/notifications/my/unread-count")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount", is(1)));

        // Step 3: User A gets notifications feed
        MvcResult feedResult = mockMvc.perform(get("/api/v1/notifications/my")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].read", is(false)))
                .andReturn();

        JsonNode feedNode = objectMapper.readTree(feedResult.getResponse().getContentAsString());
        long notifId = feedNode.path("data").get(0).path("id").asLong();

        // Step 4: User B attempts to mark User A's notification as read -> 403 Forbidden
        mockMvc.perform(put("/api/v1/notifications/" + notifId + "/read")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail", containsString("Access denied")));

        // Step 5: User A marks notification as read -> 200 OK
        mockMvc.perform(put("/api/v1/notifications/" + notifId + "/read")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.read", is(true)));

        // Step 6: User A checks unread count -> 0
        mockMvc.perform(get("/api/v1/notifications/my/unread-count")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount", is(0)));

        // Step 7: Executive Dashboard API
        mockMvc.perform(get("/api/v1/reports/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalStudents", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.currentSessionName", notNullValue()));

        // Step 8: Students CSV Export
        MvcResult csvResult = mockMvc.perform(get("/api/v1/reports/students/export-csv")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("students_export.csv")))
                .andReturn();

        String csvContent = csvResult.getResponse().getContentAsString();
        assertTrue(csvContent.contains("Admission Number"), "CSV header must contain 'Admission Number'");

        // Step 9: Verify Formula Injection Sanitizer (CWE-1236)
        assertEquals("\"'=1+1\"", CsvSanitizer.sanitize("=1+1"));
        assertEquals("\"'+cmd|' /C calc'!A0\"", CsvSanitizer.sanitize("+cmd|' /C calc'!A0"));
        assertEquals("\"'@SUM(1,2)\"", CsvSanitizer.sanitize("@SUM(1,2)"));
        assertEquals("\"'-5\"", CsvSanitizer.sanitize("-5"));
        assertEquals("Muhammad Ali", CsvSanitizer.sanitize("Muhammad Ali"));
    }
}
