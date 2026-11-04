package com.bluebells.dataimport;

import com.bluebells.academics.*;
import com.bluebells.dataimport.dto.ConfirmImportRequest;
import com.bluebells.dataimport.entity.DataImportJob;
import com.bluebells.dataimport.repository.DataImportJobRepository;
import com.bluebells.dataimport.repository.DataImportJobRowRepository;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DataImportValidationAndExecutionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianStudentLinkRepository linkRepository;

    @Autowired
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private GradeLevelRepository gradeLevelRepository;

    @Autowired
    private DataImportJobRepository jobRepository;

    @Autowired
    private DataImportJobRowRepository rowRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private AcademicSession testSession;
    private ClassEntity testClass;
    private Section testSection;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Admin")));

        User admin = userRepository.findByUsername("superadmin_test_exec").orElseGet(() -> {
            User u = new User();
            u.setUsername("superadmin_test_exec");
            u.setEmail("superadmin_exec@test.com");
            u.setFullName("Super Administrator Exec");
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            u.setStatus("ACTIVE");
            u.setRoles(new HashSet<>(Set.of(adminRole)));
            return userRepository.save(u);
        });
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));

        // Ensure session, grade level, class, and section exist for tests
        testSession = sessionRepository.findByName("2026-2027").orElseGet(() -> {
            AcademicSession s = new AcademicSession("2026-2027", LocalDate.of(2026, 8, 1), LocalDate.of(2027, 5, 31), true);
            return sessionRepository.save(s);
        });

        GradeLevel grade = gradeLevelRepository.findByName("Secondary").orElseGet(() ->
                gradeLevelRepository.save(new GradeLevel("Secondary", "SECONDARY", 9)));

        testClass = classRepository.findByName("Class 9").orElseGet(() ->
                classRepository.save(new ClassEntity(grade, "Class 9", "CLS-9")));

        testSection = sectionRepository.findByClassEntityIdAndName(testClass.getId(), "Section A").orElseGet(() ->
                sectionRepository.save(new Section(testClass, "Section A", "Room 101", 35)));

        // Clean up test students from previous runs
        for (String adm : java.util.List.of("TEST-ADM-001", "TEST-ADM-002", "TEST-FORMULA-1", "TEST-DUP-01")) {
            studentRepository.findByAdmissionNumber(adm).ifPresent(s -> {
                linkRepository.deleteAll(linkRepository.findByStudentId(s.getId()));
                enrollmentRepository.deleteAll(enrollmentRepository.findByStudentId(s.getId()));
                studentRepository.delete(s);
            });
        }
    }

    @Test
    @DisplayName("Download official templates returns valid content and headers")
    void testDownloadTemplates() throws Exception {
        // Student Excel
        mockMvc.perform(get("/api/v1/admin/data-import/templates/student.xlsx")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"bluebells_student_import_template.xlsx\""))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        // Student CSV
        mockMvc.perform(get("/api/v1/admin/data-import/templates/student.csv")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"bluebells_student_import_template.csv\""))
                .andExpect(content().contentType("text/csv; charset=UTF-8"));

        // Staff Excel
        mockMvc.perform(get("/api/v1/admin/data-import/templates/staff.xlsx")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"bluebells_staff_import_template.xlsx\""))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        // Staff CSV
        mockMvc.perform(get("/api/v1/admin/data-import/templates/staff.csv")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"bluebells_staff_import_template.csv\""))
                .andExpect(content().contentType("text/csv; charset=UTF-8"));
    }

    @Test
    @DisplayName("Upload, validate, and execute student import in STRICT mode")
    void testStudentUploadValidationAndExecution() throws Exception {
        String csvContent = """
                admission_number,first_name,last_name,gender,date_of_birth,blood_group,b_form_number,residential_address,admission_date,academic_session,class_name,section_name,roll_number,guardian_name,guardian_relation,guardian_phone,guardian_cnic,guardian_occupation,guardian_address
                TEST-ADM-001,Mustafa,Kamal,MALE,2012-05-10,B+,42101-1111111-1,Karachi Block 2,2024-08-01,2026-2027,Class 9,Section A,11,Kamal Pasha,FATHER,+92-300-9988776,42101-9988776-1,Architect,Karachi Block 2
                TEST-ADM-002,Maryam,Nawaz,FEMALE,2012-07-15,O+,42101-2222222-2,Clifton Karachi,2024-08-01,2026-2027,Class 9,Section A,12,Nawaz Sharif,FATHER,+92-321-8877665,42101-8877665-2,Manager,Clifton Karachi
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "students.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        // 1. Upload
        MvcResult uploadRes = mockMvc.perform(multipart("/api/v1/admin/data-import/upload")
                        .file(file)
                        .param("importType", "STUDENT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.jobId").exists())
                .andReturn();

        String responseBody = uploadRes.getResponse().getContentAsString();
        String jobId = objectMapper.readTree(responseBody).path("data").path("jobId").asText();
        assertThat(jobId).isNotBlank();

        // 2. Validate
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/validate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.validRows").value(2))
                .andExpect(jsonPath("$.data.invalidRows").value(0))
                .andExpect(jsonPath("$.data.status").value("VALIDATED"));

        // 3. Confirm Import (STRICT mode)
        ConfirmImportRequest confirmReq = new ConfirmImportRequest("STRICT", "Test student import");
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/confirm")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.importedRows").value(2))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        // Verify Student in Database
        Optional<Student> s1 = studentRepository.findByAdmissionNumber("TEST-ADM-001");
        assertThat(s1).isPresent();
        assertThat(s1.get().getFirstName()).isEqualTo("Mustafa");
        assertThat(s1.get().getLastName()).isEqualTo("Kamal");

        // Verify Guardian link and enrollment
        Optional<Guardian> g1 = guardianRepository.findByPhonePrimary("+92-300-9988776");
        assertThat(g1).isPresent();
        assertThat(g1.get().getFullName()).isEqualTo("Kamal Pasha");

        assertThat(linkRepository.findByStudentId(s1.get().getId())).isNotEmpty();
        assertThat(enrollmentRepository.findByStudentIdAndAcademicSessionId(s1.get().getId(), testSession.getId())).isPresent();
    }

    @Test
    @DisplayName("Formula injection strings are safely sanitized and neutralized")
    void testFormulaInjectionSanitization() throws Exception {
        String csvContent = """
                admission_number,first_name,last_name,gender,date_of_birth,blood_group,b_form_number,residential_address,admission_date,academic_session,class_name,section_name,roll_number,guardian_name,guardian_relation,guardian_phone,guardian_cnic,guardian_occupation,guardian_address
                TEST-FORMULA-1,=SUM(1+1),Normal,MALE,2012-05-10,B+,42101-1111111-3,Address 1,2024-08-01,2026-2027,Class 9,Section A,15,Guardian One,FATHER,+92-300-1112233,42101-1112233-1,Business,Address 1
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "formula_test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadRes = mockMvc.perform(multipart("/api/v1/admin/data-import/upload")
                        .file(file)
                        .param("importType", "STUDENT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        String jobId = objectMapper.readTree(uploadRes.getResponse().getContentAsString()).path("data").path("jobId").asText();

        // Validate
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/validate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.validRows").value(1));

        // Confirm
        ConfirmImportRequest req = new ConfirmImportRequest("STRICT", "Formula test");
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/confirm")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Verify student first name does NOT start with '='
        Optional<Student> student = studentRepository.findByAdmissionNumber("TEST-FORMULA-1");
        assertThat(student).isPresent();
        assertThat(student.get().getFirstName()).doesNotStartWith("=");
    }

    @Test
    @DisplayName("In-file duplicate detection flags duplicate admission numbers")
    void testDuplicateDetection() throws Exception {
        String csvContent = """
                admission_number,first_name,last_name,gender,date_of_birth,blood_group,b_form_number,residential_address,admission_date,academic_session,class_name,section_name,roll_number,guardian_name,guardian_relation,guardian_phone,guardian_cnic,guardian_occupation,guardian_address
                TEST-DUP-01,Ali,Ahmad,MALE,2012-05-10,B+,42101-1111111-4,Address 1,2024-08-01,2026-2027,Class 9,Section A,18,Guardian A,FATHER,+92-300-2223344,42101-2223344-1,Business,Address 1
                TEST-DUP-01,Ali,Copy,MALE,2012-05-10,B+,42101-1111111-4,Address 1,2024-08-01,2026-2027,Class 9,Section A,19,Guardian A,FATHER,+92-300-2223344,42101-2223344-1,Business,Address 1
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "duplicate_test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadRes = mockMvc.perform(multipart("/api/v1/admin/data-import/upload")
                        .file(file)
                        .param("importType", "STUDENT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        String jobId = objectMapper.readTree(uploadRes.getResponse().getContentAsString()).path("data").path("jobId").asText();

        // Validate
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/validate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicateRows").value(1))
                .andExpect(jsonPath("$.data.validRows").value(1));

        // Strict mode must reject import because of duplicate row
        ConfirmImportRequest strictReq = new ConfirmImportRequest("STRICT", "Strict mode rejection test");
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/confirm")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(strictReq)))
                .andExpect(status().is4xxClientError());

        // Valid-Only mode succeeds and imports the valid row while skipping duplicate
        ConfirmImportRequest validOnlyReq = new ConfirmImportRequest("VALID_ONLY", "Valid only mode test");
        mockMvc.perform(post("/api/v1/admin/data-import/" + jobId + "/confirm")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOnlyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.importedRows").value(1))
                .andExpect(jsonPath("$.data.skippedRows").value(1));
    }
}
