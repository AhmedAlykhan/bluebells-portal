package com.bluebells.certificates;

import com.bluebells.certificates.dto.IssueCertificateRequest;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CertificateServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CertificateTemplateRepository templateRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private Long studentId;
    private Long templateId;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));

        User admin = userRepository.findByUsername("cert_admin_test").orElseGet(() -> {
            User u = new User();
            u.setUsername("cert_admin_test");
            u.setEmail("cert_admin@bluebells.test");
            u.setFullName("Certificate Administrator");
            u.setPasswordHash(passwordEncoder.encode("TestPass@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        Student student = studentRepository.findByAdmissionNumber("BSS-CERT-001").orElseGet(() -> {
            Student s = new Student();
            s.setAdmissionNumber("BSS-CERT-001");
            s.setFirstName("Hamza");
            s.setLastName("Siddiqui");
            s.setDateOfBirth(LocalDate.of(2012, 1, 15));
            s.setGender("MALE");
            s.setResidentialAddress("Qasimabad Hyderabad");
            s.setAdmissionDate(LocalDate.of(2024, 8, 1));
            s.setStatus("ACTIVE");
            return studentRepository.save(s);
        });
        studentId = student.getId();

        CertificateTemplate tpl = templateRepository.findByCertificateTypeAndActiveTrue("MERIT").orElseGet(() -> {
            CertificateTemplate t = new CertificateTemplate();
            t.setTitle("Certificate of Academic Merit");
            t.setCertificateType("MERIT");
            t.setHeaderText("BLUEBELLS SCHOOLING SYSTEM");
            t.setBodyTemplate("This is proudly awarded to {{STUDENT_NAME}} for outstanding academic distinction in the academic session {{SESSION}}.");
            t.setDefaultSignatoryName("Huma Arif");
            t.setDefaultSignatoryTitle("Principal");
            t.setBorderStyle("ROYAL_GOLD");
            t.setActive(true);
            return templateRepository.save(t);
        });
        templateId = tpl.getId();
    }

    @Test
    @DisplayName("Admin can view certificate templates")
    void testGetCertificateTemplates() throws Exception {
        mockMvc.perform(get("/api/v1/certificates/templates")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].certificateType").exists());
    }

    @Test
    @DisplayName("Admin can issue digital certificate with dynamic placeholder replacement")
    void testIssueCertificateSuccess() throws Exception {
        IssueCertificateRequest req = new IssueCertificateRequest();
        req.setTemplateId(templateId);
        req.setStudentId(studentId);
        req.setCertificateType("MERIT");
        req.setCustomTitle("Top Performer in Mathematics");
        req.setIssueDate(LocalDate.of(2026, 2, 15));

        MvcResult result = mockMvc.perform(post("/api/v1/certificates/issue")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.certificateNumber").value(startsWith("BSS-CERT-")))
                .andExpect(jsonPath("$.data.verificationCode").isString())
                .andExpect(jsonPath("$.data.recipientName").value("Hamza Siddiqui"))
                .andExpect(jsonPath("$.data.narrative").value(containsString("Hamza Siddiqui")))
                .andExpect(jsonPath("$.data.status").value("ISSUED"))
                .andReturn();

        String verCode = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("verificationCode").asText();

        // Public verification test without login credentials
        mockMvc.perform(get("/api/v1/certificates/verify/" + verCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authentic").value(true))
                .andExpect(jsonPath("$.data.recipientName").value("Hamza Siddiqui"))
                .andExpect(jsonPath("$.data.message").value(containsString("Bluebells")));
    }

    @Test
    @DisplayName("Admin can revoke an issued certificate")
    void testRevokeCertificate() throws Exception {
        IssueCertificateRequest req = new IssueCertificateRequest();
        req.setTemplateId(templateId);
        req.setStudentId(studentId);
        req.setCertificateType("MERIT");
        req.setCustomTitle("Participation in Science Fair");
        req.setIssueDate(LocalDate.of(2026, 2, 1));

        MvcResult result = mockMvc.perform(post("/api/v1/certificates/issue")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        long certId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(post("/api/v1/certificates/" + certId + "/revoke")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("reason", "Issued with incorrect category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
