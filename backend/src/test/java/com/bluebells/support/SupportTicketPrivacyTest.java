package com.bluebells.support;

import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
import com.bluebells.support.dto.CreateTicketRequest;
import com.bluebells.support.dto.ReplyTicketRequest;
import com.bluebells.support.dto.UpdateTicketStatusRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SupportTicketPrivacyTest {

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

    private String parentAToken;
    private String parentBToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));

        User parentA = userRepository.findByUsername("ticket_parent_a").orElseGet(() -> {
            User u = new User();
            u.setUsername("ticket_parent_a");
            u.setEmail("parent_a@bluebells.test");
            u.setFullName("Parent Alpha");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(parentRole)));
            return userRepository.save(u);
        });
        parentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(parentA));

        User parentB = userRepository.findByUsername("ticket_parent_b").orElseGet(() -> {
            User u = new User();
            u.setUsername("ticket_parent_b");
            u.setEmail("parent_b@bluebells.test");
            u.setFullName("Parent Beta");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(parentRole)));
            return userRepository.save(u);
        });
        parentBToken = jwtTokenProvider.generateToken(UserPrincipal.create(parentB));

        User admin = userRepository.findByUsername("ticket_admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("ticket_admin");
            u.setEmail("ticket_admin@bluebells.test");
            u.setFullName("Support Admin");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));
    }

    @Test
    @DisplayName("Parent can create support ticket and view it in their list")
    void testParentCreateAndFetchOwnTicket() throws Exception {
        CreateTicketRequest req = new CreateTicketRequest();
        req.setCategory("FEE");
        req.setSubject("Fee challan bank branch inquiry");
        req.setDescription("Which Askari Bank branches accept school tuition fees without surcharge?");
        req.setPriority("MEDIUM");

        MvcResult result = mockMvc.perform(post("/api/v1/support/tickets")
                        .header("Authorization", "Bearer " + parentAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ticketNumber").value(startsWith("TKT-")))
                .andExpect(jsonPath("$.data.subject").value("Fee challan bank branch inquiry"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andReturn();

        // Parent A sees their own tickets
        mockMvc.perform(get("/api/v1/support/tickets")
                        .header("Authorization", "Bearer " + parentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Strict Privacy Boundary: Parent B cannot access Parent A's ticket (403 Forbidden)")
    void testParentCannotAccessAnotherParentsTicket() throws Exception {
        // Parent A creates ticket
        CreateTicketRequest req = new CreateTicketRequest();
        req.setCategory("ACADEMIC");
        req.setSubject("Confidential academic query");
        req.setDescription("Parent A inquiry regarding math teacher feedback");
        req.setPriority("HIGH");

        MvcResult result = mockMvc.perform(post("/api/v1/support/tickets")
                        .header("Authorization", "Bearer " + parentAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long ticketId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Parent B attempts to access Parent A's ticket -> Must be 403 Forbidden
        mockMvc.perform(get("/api/v1/support/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + parentBToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Internal staff notes are filtered out and completely invisible to parent")
    void testStaffInternalNotesHiddenFromParent() throws Exception {
        // Parent A creates ticket
        CreateTicketRequest req = new CreateTicketRequest();
        req.setCategory("ADMINISTRATIVE");
        req.setSubject("Document request for transfer certificate");
        req.setDescription("Requesting provisional character certificate");
        req.setPriority("LOW");

        MvcResult result = mockMvc.perform(post("/api/v1/support/tickets")
                        .header("Authorization", "Bearer " + parentAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long ticketId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Admin posts internal staff note
        ReplyTicketRequest internalNote = new ReplyTicketRequest();
        internalNote.setMessage("Internal note: Clearance pending from accounts department.");
        internalNote.setInternalNote(true);

        mockMvc.perform(post("/api/v1/support/tickets/" + ticketId + "/messages")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(internalNote)))
                .andExpect(status().isOk());

        // Admin views ticket -> Sees the internal note
        mockMvc.perform(get("/api/v1/support/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.messages[?(@.message contains 'Clearance pending')]", hasSize(1)));

        // Parent A views ticket -> Internal note must NOT be present
        mockMvc.perform(get("/api/v1/support/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + parentAToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.messages[?(@.message contains 'Clearance pending')]", hasSize(0)));
    }

    @Test
    @DisplayName("Staff can resolve ticket with resolution notes and status change")
    void testStaffCanResolveTicket() throws Exception {
        CreateTicketRequest req = new CreateTicketRequest();
        req.setCategory("TECHNICAL");
        req.setSubject("Cannot download timetable PDF");
        req.setDescription("Receiving 404 error when clicking timetable link");
        req.setPriority("MEDIUM");

        MvcResult result = mockMvc.perform(post("/api/v1/support/tickets")
                        .header("Authorization", "Bearer " + parentAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long ticketId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        UpdateTicketStatusRequest statusReq = new UpdateTicketStatusRequest();
        statusReq.setStatus("RESOLVED");
        statusReq.setResolutionNotes("Resolved: Regenerated PDF file and updated storage link.");

        mockMvc.perform(patch("/api/v1/support/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RESOLVED"))
                .andExpect(jsonPath("$.data.resolutionNotes").value(containsString("Regenerated PDF")));
    }
}
