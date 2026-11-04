package com.bluebells.finance;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.finance.dto.*;
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
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FeeInvoiceAndPaymentLifecycleTest {

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
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository linkRepository;

    @Autowired
    private FeeCategoryRepository categoryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String parentAToken;

    private AcademicSession currentSession;
    private ClassEntity schoolClass;
    private FeeCategory tuitionCategory;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        Role parentRole = roleRepository.findByName("ROLE_PARENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PARENT", "Parent")));

        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        User userA = userRepository.findByUsername("parent_fin_a")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("parent_fin_a");
                    u.setEmail("parent.fin.a@test.com");
                    u.setFullName("Parent Finance A");
                    u.setPasswordHash(passwordEncoder.encode("Parent@12345"));
                    u.setStatus("ACTIVE");
                    u.setRoles(Set.of(parentRole));
                    return userRepository.save(u);
                });
        parentAToken = jwtTokenProvider.generateToken(UserPrincipal.create(userA));

        Guardian guardianA = guardianRepository.findByUserId(userA.getId())
                .orElseGet(() -> {
                    Guardian g = new Guardian("Parent Finance A", "FATHER", "+92 300 6666661", "41303-6666661-1");
                    g.setUser(userA);
                    return guardianRepository.save(g);
                });

        currentSession = sessionRepository.findByCurrentTrue().orElseThrow();
        schoolClass = classRepository.findAll().get(0);
        Section sectionA = sectionRepository.findAll().get(0);
        tuitionCategory = categoryRepository.findByName("Monthly Tuition Fee")
                .orElseGet(() -> categoryRepository.save(new FeeCategory("Monthly Tuition Fee", true)));

        student1 = new Student();
        student1.setAdmissionNumber("BSS-FIN-001");
        student1.setFirstName("Hamza");
        student1.setLastName("Mir");
        student1.setDateOfBirth(LocalDate.of(2011, 4, 15));
        student1.setGender("MALE");
        student1.setResidentialAddress("Latifabad Unit 2, Hyderabad");
        student1.setAdmissionDate(LocalDate.now());
        student1.setStatus("ACTIVE");
        student1 = studentRepository.save(student1);

        student2 = new Student();
        student2.setAdmissionNumber("BSS-FIN-002");
        student2.setFirstName("Fatima");
        student2.setLastName("Zahra");
        student2.setDateOfBirth(LocalDate.of(2011, 7, 22));
        student2.setGender("FEMALE");
        student2.setResidentialAddress("Autobahn Road, Hyderabad");
        student2.setAdmissionDate(LocalDate.now());
        student2.setStatus("ACTIVE");
        student2 = studentRepository.save(student2);

        enrollmentRepository.save(new Enrollment(student1, currentSession, sectionA, "10", LocalDate.now()));
        enrollmentRepository.save(new Enrollment(student2, currentSession, sectionA, "11", LocalDate.now()));

        linkRepository.save(new GuardianStudentLink(guardianA, student1, true, true));
    }

    @Test
    @DisplayName("Block 8 Verification: Complete Fee Structure, Invoicing, Partial Payment, FIFO Allocation, and Parent Isolation Lifecycle")
    void testCompleteFeeAndFinanceLifecycle() throws Exception {
        // Step 1: Configure Fee Structure (Class 1, Monthly Tuition Fee = Rs. 3500.00)
        CreateFeeStructureRequest fsReq = new CreateFeeStructureRequest(
                currentSession.getId(),
                schoolClass.getId(),
                tuitionCategory.getId(),
                new BigDecimal("3500.00"),
                "MONTHLY"
        );

        mockMvc.perform(post("/api/v1/finance/structures")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fsReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.amount", is(3500.00)))
                .andExpect(jsonPath("$.data.frequency", is("MONTHLY")));

        // Step 2: Verify Financial Invariant - Discount exceeding total amount rejected with 400 Bad Request
        CreateInvoiceRequest invalidInvoice = new CreateInvoiceRequest(
                student1.getId(),
                currentSession.getId(),
                "2026-11",
                LocalDate.now().plusDays(10),
                new BigDecimal("5000.00"),
                List.of(new CreateInvoiceItemRequest(tuitionCategory.getId(), new BigDecimal("3500.00")))
        );

        mockMvc.perform(post("/api/v1/finance/invoices")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInvoice)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Discount amount cannot exceed total invoice amount")));

        // Step 3: Batch Generate Monthly Invoices for November 2026
        GenerateMonthlyInvoicesRequest batchReq = new GenerateMonthlyInvoicesRequest(
                currentSession.getId(),
                schoolClass.getId(),
                "2026-11",
                LocalDate.now().plusDays(15)
        );

        mockMvc.perform(post("/api/v1/finance/invoices/generate-monthly")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));

        // Step 4: Query Student 1's generated invoice
        MvcResult invoicesResult = mockMvc.perform(get("/api/v1/finance/students/" + student1.getId() + "/invoices")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].totalAmount").value(3500.00))
                .andExpect(jsonPath("$.data[0].paidAmount").value(0))
                .andExpect(jsonPath("$.data[0].balanceDue").value(3500.00))
                .andExpect(jsonPath("$.data[0].status", is("UNPAID")))
                .andReturn();

        JsonNode invNode = objectMapper.readTree(invoicesResult.getResponse().getContentAsString()).path("data").get(0);
        long invoiceId = invNode.path("id").asLong();

        // Step 5: Record Partial Payment (Rs. 2000.00 out of Rs. 3500.00)
        RecordPaymentRequest partialPay = new RecordPaymentRequest(
                student1.getId(),
                new BigDecimal("2000.00"),
                LocalDate.now(),
                "CASH",
                "Partial fee payment installment 1",
                invoiceId
        );

        mockMvc.perform(post("/api/v1/finance/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialPay)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.amount", is(2000.00)))
                .andExpect(jsonPath("$.data.status", is("CLEARED")))
                .andExpect(jsonPath("$.data.allocations[0].allocatedAmount", is(2000.00)));

        // Step 6: Verify Invoice Transition to PARTIALLY_PAID
        mockMvc.perform(get("/api/v1/finance/invoices/" + invoiceId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paidAmount", is(2000.00)))
                .andExpect(jsonPath("$.data.balanceDue", is(1500.00)))
                .andExpect(jsonPath("$.data.status", is("PARTIALLY_PAID")));

        // Step 7: Record Final Settlement Payment (Rs. 1500.00 remaining)
        RecordPaymentRequest finalPay = new RecordPaymentRequest(
                student1.getId(),
                new BigDecimal("1500.00"),
                LocalDate.now(),
                "BANK_TRANSFER",
                "Final settlement payment",
                invoiceId
        );

        mockMvc.perform(post("/api/v1/finance/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finalPay)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.amount", is(1500.00)));

        // Step 8: Verify Invoice Transition to PAID (balanceDue == 0.00)
        mockMvc.perform(get("/api/v1/finance/invoices/" + invoiceId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paidAmount").value(3500.00))
                .andExpect(jsonPath("$.data.balanceDue").value(0))
                .andExpect(jsonPath("$.data.status", is("PAID")));

        // Step 9: Verify Overpayment Guard on Fully Paid Invoice (returns 400 Bad Request)
        RecordPaymentRequest excessPay = new RecordPaymentRequest(
                student1.getId(),
                new BigDecimal("500.00"),
                LocalDate.now(),
                "CASH",
                "Excess payment attempt",
                invoiceId
        );

        mockMvc.perform(post("/api/v1/finance/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excessPay)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("already fully paid")));

        // Step 10: Verify Row-Level Parent Financial Isolation
        // Parent A is linked to Student 1 -> Can view Student 1's invoices and payment receipts
        mockMvc.perform(get("/api/v1/finance/students/" + student1.getId() + "/invoices")
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/api/v1/finance/students/" + student1.getId() + "/payments")
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        // Parent A is NOT linked to Student 2 -> Forbidden 403 on invoices & payments
        mockMvc.perform(get("/api/v1/finance/students/" + student2.getId() + "/invoices")
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail", containsString("Access denied")));

        mockMvc.perform(get("/api/v1/finance/students/" + student2.getId() + "/payments")
                        .header("Authorization", "Bearer " + parentAToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail", containsString("Access denied")));
    }
}
