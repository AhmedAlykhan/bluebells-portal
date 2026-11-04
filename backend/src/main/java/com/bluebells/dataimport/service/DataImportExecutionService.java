package com.bluebells.dataimport.service;

import com.bluebells.academics.*;
import com.bluebells.dataimport.dto.ConfirmImportRequest;
import com.bluebells.dataimport.dto.DataImportJobSummaryDto;
import com.bluebells.dataimport.dto.ImportStatisticsDto;
import com.bluebells.dataimport.entity.DataImportJob;
import com.bluebells.dataimport.entity.DataImportJobRow;
import com.bluebells.dataimport.repository.DataImportErrorRepository;
import com.bluebells.dataimport.repository.DataImportJobRepository;
import com.bluebells.dataimport.repository.DataImportJobRowRepository;
import com.bluebells.guardians.*;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.staff.Staff;
import com.bluebells.staff.StaffRepository;
import com.bluebells.staff.TeacherAssignment;
import com.bluebells.staff.TeacherAssignmentRepository;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DataImportExecutionService {

    private static final Logger log = LoggerFactory.getLogger(DataImportExecutionService.class);

    private final DataImportJobRepository jobRepository;
    private final DataImportJobRowRepository rowRepository;
    private final DataImportErrorRepository errorRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository guardianStudentLinkRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final AcademicSessionRepository sessionRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataImportAuditService auditService;
    private final ObjectMapper objectMapper;

    public DataImportExecutionService(DataImportJobRepository jobRepository,
                                      DataImportJobRowRepository rowRepository,
                                      DataImportErrorRepository errorRepository,
                                      StudentRepository studentRepository,
                                      EnrollmentRepository enrollmentRepository,
                                      GuardianRepository guardianRepository,
                                      GuardianStudentLinkRepository guardianStudentLinkRepository,
                                      StaffRepository staffRepository,
                                      UserRepository userRepository,
                                      RoleRepository roleRepository,
                                      TeacherAssignmentRepository teacherAssignmentRepository,
                                      AcademicSessionRepository sessionRepository,
                                      ClassRepository classRepository,
                                      SectionRepository sectionRepository,
                                      SubjectRepository subjectRepository,
                                      PasswordEncoder passwordEncoder,
                                      DataImportAuditService auditService,
                                      ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.rowRepository = rowRepository;
        this.errorRepository = errorRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.guardianRepository = guardianRepository;
        this.guardianStudentLinkRepository = guardianStudentLinkRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
        this.sessionRepository = sessionRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    // =========================================================================
    // IMPORT EXECUTION
    // =========================================================================

    @Transactional
    public DataImportJobSummaryDto confirmImport(String jobId, ConfirmImportRequest request, User confirmedBy, String clientIp, String userAgent) {
        DataImportJob job = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));

        if (!"VALIDATED".equals(job.getStatus())) {
            throw new IllegalStateException("Only jobs in 'VALIDATED' status can be confirmed. Current status: " + job.getStatus());
        }

        String mode = request != null && request.getImportMode() != null ? request.getImportMode().toUpperCase() : "STRICT";
        job.setImportMode(mode);
        if (request != null && request.getNotes() != null) {
            job.setSummaryNotes(request.getNotes());
        }

        // Mode enforcement: STRICT mode rejects if any invalid or duplicate rows exist
        if ("STRICT".equalsIgnoreCase(mode) && (job.getInvalidRows() > 0 || job.getDuplicateRows() > 0)) {
            String msg = String.format("STRICT MODE REJECTION: Job contains %d invalid and %d duplicate rows. Please fix the file or choose 'Import Valid Only' mode.",
                    job.getInvalidRows(), job.getDuplicateRows());
            auditService.logAudit(job, "IMPORT_REJECTED_STRICT", "FAILURE", msg, clientIp, userAgent);
            throw new IllegalStateException(msg);
        }

        job.setStatus("PROCESSING");
        job.setConfirmedBy(confirmedBy);
        jobRepository.save(job);

        List<DataImportJobRow> rows = rowRepository.findByJobIdOrderByRowNumberAsc(job.getId());

        int importedCount = 0;
        int skippedCount = 0;

        if ("STUDENT".equalsIgnoreCase(job.getImportType())) {
            for (DataImportJobRow row : rows) {
                if ("VALID".equals(row.getStatus()) || "WARNING".equals(row.getStatus())) {
                    try {
                        importStudentRow(row);
                        row.setStatus("IMPORTED");
                        importedCount++;
                    } catch (Exception e) {
                        log.error("Failed to import student row {}: {}", row.getRowNumber(), e.getMessage(), e);
                        row.setStatus("FAILED");
                        row.setErrorMessage("Import execution error: " + e.getMessage());
                        skippedCount++;
                    }
                } else {
                    row.setStatus("SKIPPED");
                    skippedCount++;
                }
            }
        } else {
            for (DataImportJobRow row : rows) {
                if ("VALID".equals(row.getStatus()) || "WARNING".equals(row.getStatus())) {
                    try {
                        importStaffRow(row);
                        row.setStatus("IMPORTED");
                        importedCount++;
                    } catch (Exception e) {
                        log.error("Failed to import staff row {}: {}", row.getRowNumber(), e.getMessage(), e);
                        row.setStatus("FAILED");
                        row.setErrorMessage("Import execution error: " + e.getMessage());
                        skippedCount++;
                    }
                } else {
                    row.setStatus("SKIPPED");
                    skippedCount++;
                }
            }
        }

        rowRepository.saveAll(rows);

        job.setImportedRows(importedCount);
        job.setSkippedRows(skippedCount);
        job.setCompletedAt(LocalDateTime.now());
        job.setStatus(skippedCount > 0 ? "COMPLETED_WITH_ERRORS" : "COMPLETED");
        DataImportJob savedJob = jobRepository.save(job);

        auditService.logAudit(savedJob, "IMPORT_EXECUTED", "SUCCESS",
                String.format("Import executed in %s mode: Imported=%d, Skipped=%d", mode, importedCount, skippedCount),
                clientIp, userAgent);

        return mapToSummaryDto(savedJob);
    }

    @Transactional
    public void cancelImport(String jobId, User cancelledBy, String clientIp, String userAgent) {
        DataImportJob job = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));

        if ("COMPLETED".equals(job.getStatus()) || "PROCESSING".equals(job.getStatus())) {
            throw new IllegalStateException("Cannot cancel an import job that is already " + job.getStatus());
        }

        job.setStatus("CANCELLED");
        jobRepository.save(job);

        auditService.logAudit(job, "IMPORT_CANCELLED", "SUCCESS",
                "Import job cancelled by " + (cancelledBy != null ? cancelledBy.getUsername() : "system"),
                clientIp, userAgent);
    }

    // =========================================================================
    // STUDENT ROW PERSISTENCE
    // =========================================================================

    private void importStudentRow(DataImportJobRow row) {
        Map<String, String> data = fromJson(row.getRowDataJson());

        String admissionNumber = sanitize(data.get("admission_number"));
        String firstName = sanitize(data.get("first_name"));
        String lastName = sanitize(data.get("last_name"));
        String gender = sanitize(data.get("gender"));
        String dobStr = sanitize(data.get("date_of_birth"));
        String bloodGroup = sanitize(data.get("blood_group"));
        String bForm = sanitize(data.get("b_form_number"));
        String resAddress = sanitize(data.get("residential_address"));
        String admDateStr = sanitize(data.get("admission_date"));
        String sessionName = sanitize(data.get("academic_session"));
        String className = sanitize(data.get("class_name"));
        String sectionName = sanitize(data.get("section_name"));
        String rollNumber = sanitize(data.get("roll_number"));
        String guardianName = sanitize(data.get("guardian_name"));
        String guardianRelation = sanitize(data.get("guardian_relation"));
        String guardianPhone = sanitize(data.get("guardian_phone"));
        String guardianCnic = sanitize(data.get("guardian_cnic"));
        String guardianOccupation = sanitize(data.get("guardian_occupation"));
        String guardianAddress = sanitize(data.get("guardian_address"));

        // 1. Create Student
        Student student = new Student();
        student.setAdmissionNumber(admissionNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGender(gender != null ? gender.toUpperCase() : "OTHER");
        student.setDateOfBirth(parseDate(dobStr));
        student.setBloodGroup(bloodGroup);
        student.setBFormNumber(bForm);
        student.setResidentialAddress(resAddress != null ? resAddress : "Not provided");
        student.setAdmissionDate(parseDate(admDateStr) != null ? parseDate(admDateStr) : LocalDate.now());
        student.setStatus("ACTIVE");
        Student savedStudent = studentRepository.save(student);

        // 2. Resolve or Create Guardian
        Guardian guardian = null;
        if (guardianPhone != null && !guardianPhone.isBlank()) {
            guardian = guardianRepository.findByPhonePrimary(guardianPhone).orElse(null);
        }
        if (guardian == null && guardianCnic != null && !guardianCnic.isBlank()) {
            guardian = guardianRepository.findByCnicNumber(guardianCnic).orElse(null);
        }

        if (guardian == null) {
            guardian = new Guardian();
            guardian.setFullName(guardianName != null ? guardianName : "Guardian");
            guardian.setRelationshipType(guardianRelation != null ? guardianRelation.toUpperCase() : "GUARDIAN");
            guardian.setPhonePrimary(guardianPhone != null ? guardianPhone : "+92-000-0000000");
            guardian.setCnicNumber(guardianCnic);
            guardian.setOccupation(guardianOccupation);
            guardian.setAddress(guardianAddress != null && !guardianAddress.isBlank() ? guardianAddress : resAddress);
            guardian = guardianRepository.save(guardian);
        }

        // 3. Create Guardian-Student Link
        GuardianStudentLink link = new GuardianStudentLink(guardian, savedStudent, true, true);
        guardianStudentLinkRepository.save(link);

        // 4. Create Enrollment
        AcademicSession session = sessionRepository.findByName(sessionName)
                .orElseGet(() -> sessionRepository.findByCurrentTrue()
                        .orElseThrow(() -> new IllegalStateException("No active academic session found for enrollment.")));

        ClassEntity classEntity = classRepository.findByName(className)
                .or(() -> classRepository.findByCode(className))
                .orElseThrow(() -> new IllegalStateException("Class not found: " + className));

        Section section = sectionRepository.findByClassEntityIdAndName(classEntity.getId(), sectionName)
                .orElseThrow(() -> new IllegalStateException("Section '" + sectionName + "' not found under " + className));

        Enrollment enrollment = new Enrollment(savedStudent, session, section, rollNumber, savedStudent.getAdmissionDate());
        enrollmentRepository.save(enrollment);
    }

    // =========================================================================
    // STAFF ROW PERSISTENCE
    // =========================================================================

    private void importStaffRow(DataImportJobRow row) {
        Map<String, String> data = fromJson(row.getRowDataJson());

        String employeeId = data.get("employee_id");
        String fullName = data.get("full_name");
        String designation = data.get("designation");
        String department = data.get("department");
        String category = data.get("category");
        String qualification = data.get("qualification");
        String joiningDateStr = data.get("joining_date");
        String officialEmail = data.get("official_email");
        String phoneNumber = data.get("phone_number");
        String salaryStr = data.get("basic_salary");
        String status = data.get("status");
        String assignmentsStr = data.get("teacher_subject_assignments");

        // 1. Create User account
        String baseUsername = (officialEmail != null && officialEmail.contains("@"))
                ? officialEmail.substring(0, officialEmail.indexOf('@')).toLowerCase()
                : employeeId.toLowerCase();

        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix++;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(officialEmail);
        user.setFullName(fullName != null ? fullName : "Staff Member");
        user.setPhoneNumber(phoneNumber);
        // Initial random temp password hashed with BCrypt
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString().substring(0, 10)));
        user.setStatus("ACTIVE".equalsIgnoreCase(status) ? "ACTIVE" : "LOCKED");

        // Assign Role based on category / designation
        String roleName = "ROLE_STAFF";
        if ("TEACHING".equalsIgnoreCase(category)) {
            roleName = "ROLE_TEACHER";
        } else if (designation != null && (designation.toLowerCase().contains("principal") || designation.toLowerCase().contains("vice principal"))) {
            roleName = "ROLE_PRINCIPAL";
        } else if (designation != null && (designation.toLowerCase().contains("account") || (department != null && department.toLowerCase().contains("finance")))) {
            roleName = "ROLE_ACCOUNTANT";
        } else if ("ADMINISTRATIVE".equalsIgnoreCase(category)) {
            roleName = "ROLE_SCHOOL_ADMIN";
        }

        Role role = roleRepository.findByName(roleName)
                .or(() -> roleRepository.findByName("ROLE_TEACHER"))
                .orElse(null);

        if (role != null) {
            user.getRoles().add(role);
        }

        User savedUser = userRepository.save(user);

        // 2. Create Staff record
        Staff staff = new Staff();
        staff.setUser(savedUser);
        staff.setEmployeeId(employeeId);
        staff.setDesignation(designation != null ? designation : "Staff");
        staff.setDepartment(department != null ? department : "General");
        staff.setQualification(qualification);
        staff.setJoiningDate(parseDate(joiningDateStr) != null ? parseDate(joiningDateStr) : LocalDate.now());
        if (salaryStr != null && !salaryStr.isBlank()) {
            try {
                staff.setSalary(new BigDecimal(salaryStr.trim()));
            } catch (Exception ignored) {
            }
        }
        Staff savedStaff = staffRepository.save(staff);

        // 3. Teacher Assignments
        if (assignmentsStr != null && !assignmentsStr.isBlank() && "TEACHING".equalsIgnoreCase(category)) {
            AcademicSession session = sessionRepository.findByCurrentTrue().orElse(null);
            if (session != null) {
                String[] assignments = assignmentsStr.split(";");
                for (String assign : assignments) {
                    String[] parts = assign.split("\\|");
                    if (parts.length >= 3) {
                        String cls = parts[0].trim();
                        String sec = parts[1].trim();
                        String sub = parts[2].trim();

                        Optional<ClassEntity> cOpt = classRepository.findByName(cls).or(() -> classRepository.findByCode(cls));
                        if (cOpt.isPresent()) {
                            Optional<Section> secOpt = sectionRepository.findByClassEntityIdAndName(cOpt.get().getId(), sec);
                            Optional<Subject> subOpt = subjectRepository.findByClassEntityIdAndName(cOpt.get().getId(), sub)
                                    .or(() -> subjectRepository.findByClassEntityIdAndCode(cOpt.get().getId(), sub))
                                    .or(() -> subjectRepository.findByName(sub));

                            if (secOpt.isPresent() && subOpt.isPresent()) {
                                boolean exists = teacherAssignmentRepository.existsByTeacherIdAndSectionIdAndSubjectIdAndAcademicSessionId(
                                        savedUser.getId(), secOpt.get().getId(), subOpt.get().getId(), session.getId());
                                if (!exists) {
                                    TeacherAssignment ta = new TeacherAssignment(savedUser, secOpt.get(), subOpt.get(), session, false);
                                    teacherAssignmentRepository.save(ta);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // STATISTICS & REPORTS
    // =========================================================================

    public ImportStatisticsDto getStatistics() {
        ImportStatisticsDto dto = new ImportStatisticsDto();
        dto.setTotalRecordsImported(jobRepository.sumImportedRows());
        dto.setSuccessfulRecords(jobRepository.sumImportedRows());
        dto.setFailedRecords(jobRepository.sumFailedRows());
        dto.setDuplicateRecords(jobRepository.sumDuplicateRows());
        dto.setPendingJobs(jobRepository.countPendingJobs());
        dto.setTotalCompletedJobs(jobRepository.countCompletedJobs());

        jobRepository.findTopByOrderByCreatedAtDesc().ifPresent(j -> {
            dto.setLastImportDate(j.getCompletedAt() != null ? j.getCompletedAt() : j.getCreatedAt());
            dto.setLastImportUser(j.getUploadedBy() != null ? j.getUploadedBy().getFullName() : "System");
        });

        return dto;
    }

    public Page<DataImportJobSummaryDto> getHistory(Pageable pageable) {
        return jobRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapToSummaryDto);
    }

    public DataImportJobSummaryDto getJobSummary(String jobId) {
        return jobRepository.findByJobId(jobId)
                .map(this::mapToSummaryDto)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));
    }

    public byte[] generateExecutionReportCsv(String jobId) {
        DataImportJob job = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));

        List<DataImportJobRow> rows = rowRepository.findByJobIdOrderByRowNumberAsc(job.getId());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "Row Number", "Identifier", "Status", "Error / Skip Reason", "Details"
             ))) {

            for (DataImportJobRow r : rows) {
                csvPrinter.printRecord(
                        r.getRowNumber(),
                        r.getEntityIdentifier(),
                        r.getStatus(),
                        r.getErrorMessage() != null ? r.getErrorMessage() : "None",
                        r.getRowDataJson()
                );
            }

            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate CSV execution report", e);
            throw new RuntimeException("Error generating execution report: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private DataImportJobSummaryDto mapToSummaryDto(DataImportJob job) {
        DataImportJobSummaryDto dto = new DataImportJobSummaryDto();
        dto.setJobId(job.getJobId());
        dto.setImportType(job.getImportType());
        dto.setOriginalFilename(job.getOriginalFilename());
        dto.setFileFormat(job.getFileFormat());
        dto.setFileSizeBytes(job.getFileSizeBytes());
        dto.setStatus(job.getStatus());
        dto.setImportMode(job.getImportMode());
        dto.setTotalRows(job.getTotalRows());
        dto.setValidRows(job.getValidRows());
        dto.setInvalidRows(job.getInvalidRows());
        dto.setDuplicateRows(job.getDuplicateRows());
        dto.setWarningRows(job.getWarningRows());
        dto.setImportedRows(job.getImportedRows());
        dto.setSkippedRows(job.getSkippedRows());
        dto.setUploadedBy(job.getUploadedBy() != null ? job.getUploadedBy().getFullName() : "Unknown");
        dto.setConfirmedBy(job.getConfirmedBy() != null ? job.getConfirmedBy().getFullName() : null);
        dto.setSummaryNotes(job.getSummaryNotes());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setCompletedAt(job.getCompletedAt());
        return dto;
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(val.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Map<String, String> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String sanitize(String val) {
        if (val == null) return null;
        String trimmed = val.trim();
        if (trimmed.isEmpty()) return "";
        char firstChar = trimmed.charAt(0);
        if (firstChar == '=' || firstChar == '+' || firstChar == '-' || firstChar == '@' || firstChar == '\t' || firstChar == '\r') {
            return trimmed.substring(1).trim();
        }
        return trimmed;
    }
}
