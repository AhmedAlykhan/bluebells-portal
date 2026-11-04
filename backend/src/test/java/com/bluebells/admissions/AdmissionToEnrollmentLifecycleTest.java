package com.bluebells.admissions;

import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.admissions.dto.CreateApplicationRequest;
import com.bluebells.admissions.dto.EnrollApplicantRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdmissionToEnrollmentLifecycleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private Long targetClassId;
    private Long targetSectionId;

    @BeforeEach
    void setUp() {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        ClassEntity class1 = classRepository.findByCode("CLS-01")
                .orElseGet(() -> classRepository.findAll().get(0));
        targetClassId = class1.getId();

        Section secA = sectionRepository.findByClassEntityIdAndName(targetClassId, "Section A")
                .orElseGet(() -> sectionRepository.findAll().get(0));
        targetSectionId = secA.getId();
    }

    @Test
    @DisplayName("Complete Admission-to-Enrollment Lifecycle: Public submit -> Admin list -> Review -> Enroll")
    void testFullAdmissionToEnrollmentLifecycle() throws Exception {
        // 1. Public submission of admission application (No Bearer Token required)
        CreateApplicationRequest submitReq = new CreateApplicationRequest();
        submitReq.setApplicantName("Hamza Ali");
        submitReq.setDateOfBirth(LocalDate.of(2018, 5, 14));
        submitReq.setGender("MALE");
        submitReq.setAppliedClassId(targetClassId);
        submitReq.setGuardianName("Muhammad Ali");
        submitReq.setGuardianPhone("+92 300 1234567");
        submitReq.setGuardianCnic("41303-1234567-1");
        submitReq.setNotes("Transferring from City School Hyderabad");

        MvcResult submitResult = mockMvc.perform(post("/api/v1/admissions/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.applicantName", is("Hamza Ali")))
                .andExpect(jsonPath("$.data.status", is("SUBMITTED")))
                .andExpect(jsonPath("$.data.applicationNumber", startsWith("APP-")))
                .andReturn();

        Long applicationId = objectMapper.readTree(submitResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 2. Admin retrieves paginated applications
        mockMvc.perform(get("/api/v1/admissions/applications")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", not(empty())));

        // 3. Admin updates status to ACCEPTED
        mockMvc.perform(put("/api/v1/admissions/applications/" + applicationId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "status", "ACCEPTED",
                                "notes", "Interview cleared successfully"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("ACCEPTED")));

        // 4. Admin enrolls applicant as registered student
        EnrollApplicantRequest enrollReq = new EnrollApplicantRequest();
        enrollReq.setSectionId(targetSectionId);
        enrollReq.setRollNumber("01-A");
        enrollReq.setAdmissionDate(LocalDate.now());
        enrollReq.setBFormNumber("41303-9876543-1");
        enrollReq.setResidentialAddress("House 12, Block C, Latifabad Unit 2, Hyderabad");
        enrollReq.setRelationshipType("FATHER");

        mockMvc.perform(post("/api/v1/admissions/applications/" + applicationId + "/enroll")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.admissionNumber", startsWith("BSS-")))
                .andExpect(jsonPath("$.data.fullName", is("Hamza Ali")))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")))
                .andExpect(jsonPath("$.data.rollNumber", is("01-A")));

        // 5. Verify application status is now ENROLLED
        mockMvc.perform(get("/api/v1/admissions/applications/" + applicationId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("ENROLLED")));

        // 6. Attempting to enroll again should be rejected with 409 Conflict
        mockMvc.perform(post("/api/v1/admissions/applications/" + applicationId + "/enroll")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollReq)))
                .andExpect(status().isConflict());
    }
}
