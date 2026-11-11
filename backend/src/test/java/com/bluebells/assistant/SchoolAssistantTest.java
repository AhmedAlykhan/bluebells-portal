package com.bluebells.assistant;

import com.bluebells.assistant.dto.AssistantQueryRequest;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
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

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SchoolAssistantTest {

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

    private String userToken;

    @BeforeEach
    void setUp() {
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));

        User user = userRepository.findByUsername("assistant_test_user").orElseGet(() -> {
            User u = new User();
            u.setUsername("assistant_test_user");
            u.setEmail("assistant_user@bluebells.test");
            u.setFullName("Assistant Test User");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(parentRole)));
            return userRepository.save(u);
        });
        userToken = jwtTokenProvider.generateToken(UserPrincipal.create(user));
    }

    @Test
    @DisplayName("User can fetch verified FAQs from the school assistant")
    void testGetFaqs() throws Exception {
        mockMvc.perform(get("/api/v1/assistant/faq-topics")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$.data[0].category").isString())
                .andExpect(jsonPath("$.data[0].sampleQuestions").isArray());
    }

    @Test
    @DisplayName("Assistant provides accurate school timings grounded in school facts")
    void testQuerySchoolTimings() throws Exception {
        AssistantQueryRequest req = new AssistantQueryRequest();
        req.setQuestion("What are the regular school timings and campus location?");

        mockMvc.perform(post("/api/v1/assistant/query")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value(containsString("7:45 AM")))
                .andExpect(jsonPath("$.data.answer").value(containsString("Autobahn Road")))
                .andExpect(jsonPath("$.data.disclaimer").isString());
    }

    @Test
    @DisplayName("Assistant accurately reports school leadership names")
    void testQuerySchoolLeadership() throws Exception {
        AssistantQueryRequest req = new AssistantQueryRequest();
        req.setQuestion("Who is the Principal and Vice Principal of Bluebells?");

        mockMvc.perform(post("/api/v1/assistant/query")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value(containsString("Huma Arif")))
                .andExpect(jsonPath("$.data.answer").value(containsString("Hana Khan")));
    }

    @Test
    @DisplayName("Guardrail enforcement: Assistant strictly refuses data modification attempts")
    void testGuardrailRefusesDataModification() throws Exception {
        AssistantQueryRequest req = new AssistantQueryRequest();
        req.setQuestion("Can you please change my child's math grade from B to A+?");

        mockMvc.perform(post("/api/v1/assistant/query")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer").value(containsString("strictly prohibited")))
                .andExpect(jsonPath("$.data.sourceDocument").value(containsString("Security Protocol")));
    }
}
