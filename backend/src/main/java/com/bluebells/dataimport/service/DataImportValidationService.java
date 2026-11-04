package com.bluebells.dataimport.service;

import com.bluebells.academics.*;
import com.bluebells.dataimport.dto.*;
import com.bluebells.dataimport.entity.DataImportError;
import com.bluebells.dataimport.entity.DataImportJob;
import com.bluebells.dataimport.entity.DataImportJobRow;
import com.bluebells.dataimport.repository.DataImportErrorRepository;
import com.bluebells.dataimport.repository.DataImportJobRepository;
import com.bluebells.dataimport.repository.DataImportJobRowRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.staff.StaffRepository;
import com.bluebells.students.StudentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class DataImportValidationService {

    private static final Logger log = LoggerFactory.getLogger(DataImportValidationService.class);

    private static final long MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024; // 20 MB
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\-\\s()]{7,25}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z0-9\\-_/]{2,32}$");
    private static final Pattern CNIC_PATTERN = Pattern.compile("^(\\d{5}-\\d{7}-\\d|\\d{13})$");
    private static final Set<String> VALID_GENDERS = Set.of("MALE", "FEMALE", "OTHER");
    private static final Set<String> VALID_BLOOD_GROUPS = Set.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    private static final Set<String> VALID_RELATIONS = Set.of("FATHER", "MOTHER", "GUARDIAN", "UNCLE", "AUNT", "GRANDFATHER", "GRANDMOTHER");
    private static final Set<String> VALID_STAFF_CATEGORIES = Set.of("TEACHING", "NON_TEACHING", "ADMINISTRATIVE");
    private static final Set<String> VALID_STAFF_STATUSES = Set.of("ACTIVE", "PROBATION", "SUSPENDED", "TERMINATED");

    private final DataImportJobRepository jobRepository;
    private final DataImportJobRowRepository rowRepository;
    private final DataImportErrorRepository errorRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final AcademicSessionRepository sessionRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;
    private final DataImportAuditService auditService;
    private final ObjectMapper objectMapper;

    @Value("${app.import.upload-dir:uploads/import-staging}")
    private String uploadBaseDir;

    public DataImportValidationService(DataImportJobRepository jobRepository,
                                       DataImportJobRowRepository rowRepository,
                                       DataImportErrorRepository errorRepository,
                                       StudentRepository studentRepository,
                                       StaffRepository staffRepository,
                                       UserRepository userRepository,
                                       AcademicSessionRepository sessionRepository,
                                       ClassRepository classRepository,
                                       SectionRepository sectionRepository,
                                       SubjectRepository subjectRepository,
                                       DataImportAuditService auditService,
                                       ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.rowRepository = rowRepository;
        this.errorRepository = errorRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.subjectRepository = subjectRepository;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    // =========================================================================
    // 1. FILE UPLOAD & STAGING
    // =========================================================================

    @Transactional
    public DataImportJob createJob(MultipartFile file, String importType, User uploadedBy, String clientIp, String userAgent) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Upload file is required and cannot be empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds maximum allowable limit of 20MB.");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename().trim() : "import_file";
        String lowerFilename = originalFilename.toLowerCase();
        String format;
        if (lowerFilename.endsWith(".xlsx")) {
            format = "XLSX";
        } else if (lowerFilename.endsWith(".csv")) {
            format = "CSV";
        } else {
            throw new IllegalArgumentException("Unsupported file format. Only .xlsx and .csv files are supported.");
        }

        String normalizedType = importType != null ? importType.trim().toUpperCase() : "STUDENT";
        if (!"STUDENT".equals(normalizedType) && !"STAFF".equals(normalizedType)) {
            throw new IllegalArgumentException("Invalid import type: " + importType + ". Must be 'STUDENT' or 'STAFF'.");
        }

        String jobId = "JOB-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        try {
            Path targetDir = Paths.get(uploadBaseDir, jobId);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(originalFilename);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            DataImportJob job = new DataImportJob();
            job.setJobId(jobId);
            job.setImportType(normalizedType);
            job.setOriginalFilename(originalFilename);
            job.setFileFormat(format);
            job.setFileSizeBytes(file.getSize());
            job.setFilePath(targetFile.toAbsolutePath().toString());
            job.setStatus("UPLOADED");
            job.setUploadedBy(uploadedBy);
            job.setImportMode("STRICT");

            DataImportJob saved = jobRepository.save(job);
            auditService.logAudit(saved, "FILE_UPLOADED", "SUCCESS",
                    "Uploaded " + format + " file (" + file.getSize() + " bytes) for " + normalizedType + " import",
                    clientIp, userAgent);

            return saved;
        } catch (IOException e) {
            log.error("Failed to stage uploaded file", e);
            throw new RuntimeException("Could not save upload file to server: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // 2. PARSING & VALIDATION ENGINE
    // =========================================================================

    @Transactional
    public DataImportPreviewDto validateJob(String jobId, String clientIp, String userAgent) {
        DataImportJob job = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));

        // Delete existing rows and errors if re-validating
        errorRepository.deleteByJobId(job.getId());
        rowRepository.deleteByJobId(job.getId());

        job.setStatus("VALIDATING");
        jobRepository.save(job);

        File file = new File(job.getFilePath());
        if (!file.exists()) {
            job.setStatus("FAILED");
            job.setSummaryNotes("Staged file not found on disk.");
            jobRepository.save(job);
            throw new IllegalStateException("Staged file not found on disk: " + job.getFilePath());
        }

        try {
            List<Map<String, String>> rawRows;
            if ("XLSX".equalsIgnoreCase(job.getFileFormat())) {
                rawRows = parseExcel(file);
            } else {
                rawRows = parseCsv(file);
            }

            if (rawRows.isEmpty()) {
                job.setStatus("FAILED");
                job.setSummaryNotes("The uploaded file does not contain any data rows.");
                jobRepository.save(job);
                throw new IllegalArgumentException("The uploaded file does not contain any data rows.");
            }

            if ("STUDENT".equalsIgnoreCase(job.getImportType())) {
                validateStudents(job, rawRows);
            } else {
                validateStaff(job, rawRows);
            }

            job.setStatus("VALIDATED");
            jobRepository.save(job);

            auditService.logAudit(job, "VALIDATION_COMPLETED", "SUCCESS",
                    String.format("Validation completed: Total=%d, Valid=%d, Invalid=%d, Duplicate=%d, Warning=%d",
                            job.getTotalRows(), job.getValidRows(), job.getInvalidRows(), job.getDuplicateRows(), job.getWarningRows()),
                    clientIp, userAgent);

            return getJobPreview(jobId, 0, 50, null, null);
        } catch (Exception e) {
            log.error("Validation failed for job {}", jobId, e);
            job.setStatus("FAILED");
            job.setSummaryNotes("Validation error: " + e.getMessage());
            jobRepository.save(job);
            auditService.logAudit(job, "VALIDATION_FAILED", "FAILURE", e.getMessage(), clientIp, userAgent);
            throw new RuntimeException("Data validation encountered an error: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // STUDENT VALIDATION LOGIC
    // =========================================================================

    private void validateStudents(DataImportJob job, List<Map<String, String>> rawRows) {
        Set<String> seenAdmissionNumbersInFile = new HashSet<>();
        int totalRows = rawRows.size();
        int validRows = 0;
        int invalidRows = 0;
        int duplicateRows = 0;
        int warningRows = 0;

        List<DataImportJobRow> jobRowsToSave = new ArrayList<>();
        List<DataImportError> errorsToSave = new ArrayList<>();

        // Cache active session, classes, sections
        Map<String, AcademicSession> sessionMap = new HashMap<>();
        sessionRepository.findAll().forEach(s -> sessionMap.put(s.getName().trim().toLowerCase(), s));

        Map<String, ClassEntity> classMap = new HashMap<>();
        classRepository.findAll().forEach(c -> {
            classMap.put(c.getName().trim().toLowerCase(), c);
            classMap.put(c.getCode().trim().toLowerCase(), c);
        });

        for (int i = 0; i < rawRows.size(); i++) {
            int rowNum = i + 2; // Row 1 is header, data starts on row 2
            Map<String, String> row = rawRows.get(i);
            List<DataImportError> rowErrors = new ArrayList<>();

            String admissionNumber = sanitize(row.get("admission_number"));
            String firstName = sanitize(row.get("first_name"));
            String lastName = sanitize(row.get("last_name"));
            String gender = sanitize(row.get("gender"));
            String dobStr = sanitize(row.get("date_of_birth"));
            String bloodGroup = sanitize(row.get("blood_group"));
            String bForm = sanitize(row.get("b_form_number"));
            String resAddress = sanitize(row.get("residential_address"));
            String admDateStr = sanitize(row.get("admission_date"));
            String sessionName = sanitize(row.get("academic_session"));
            String className = sanitize(row.get("class_name"));
            String sectionName = sanitize(row.get("section_name"));
            String rollNumber = sanitize(row.get("roll_number"));
            String guardianName = sanitize(row.get("guardian_name"));
            String guardianRelation = sanitize(row.get("guardian_relation"));
            String guardianPhone = sanitize(row.get("guardian_phone"));
            String guardianCnic = sanitize(row.get("guardian_cnic"));

            boolean isDuplicate = false;

            // 1. Admission Number
            if (isEmpty(admissionNumber)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "admission_number", admissionNumber, "REQUIRED_FIELD", "Admission number is mandatory.", "ERROR"));
            } else if (!IDENTIFIER_PATTERN.matcher(admissionNumber).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "admission_number", admissionNumber, "INVALID_FORMAT", "Admission number must be alphanumeric (e.g. ADM-2024-001).", "ERROR"));
            } else {
                String admUpper = admissionNumber.toUpperCase();
                if (seenAdmissionNumbersInFile.contains(admUpper)) {
                    isDuplicate = true;
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "admission_number", admissionNumber, "DUPLICATE_IN_FILE", "Admission number '" + admissionNumber + "' duplicates another row in this file.", "ERROR"));
                } else {
                    seenAdmissionNumbersInFile.add(admUpper);
                }

                if (studentRepository.existsByAdmissionNumber(admissionNumber)) {
                    isDuplicate = true;
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "admission_number", admissionNumber, "DUPLICATE_IN_DB", "Admission number '" + admissionNumber + "' already exists in the school database.", "ERROR"));
                }
            }

            // 2. Names
            if (isEmpty(firstName)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "first_name", firstName, "REQUIRED_FIELD", "First name is mandatory.", "ERROR"));
            } else if (firstName.length() > 64) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "first_name", firstName, "MAX_LENGTH_EXCEEDED", "First name must not exceed 64 characters.", "ERROR"));
            }

            if (isEmpty(lastName)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "last_name", lastName, "REQUIRED_FIELD", "Last name is mandatory.", "ERROR"));
            } else if (lastName.length() > 64) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "last_name", lastName, "MAX_LENGTH_EXCEEDED", "Last name must not exceed 64 characters.", "ERROR"));
            }

            // 3. Gender
            if (isEmpty(gender)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "gender", gender, "REQUIRED_FIELD", "Gender is mandatory.", "ERROR"));
            } else if (!VALID_GENDERS.contains(gender.toUpperCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "gender", gender, "INVALID_VALUE", "Gender must be MALE, FEMALE, or OTHER.", "ERROR"));
            }

            // 4. Date of Birth
            if (isEmpty(dobStr)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "date_of_birth", dobStr, "REQUIRED_FIELD", "Date of birth is mandatory.", "ERROR"));
            } else {
                LocalDate dob = parseDate(dobStr);
                if (dob == null) {
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "date_of_birth", dobStr, "INVALID_DATE_FORMAT", "Date of birth must be in YYYY-MM-DD format.", "ERROR"));
                } else if (dob.isAfter(LocalDate.now())) {
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "date_of_birth", dobStr, "DATE_IN_FUTURE", "Date of birth cannot be in the future.", "ERROR"));
                } else if (dob.isAfter(LocalDate.now().minusYears(2))) {
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "date_of_birth", dobStr, "AGE_TOO_YOUNG", "Student must be at least 2 years old.", "ERROR"));
                }
            }

            // 5. Blood group (optional)
            if (!isEmpty(bloodGroup) && !VALID_BLOOD_GROUPS.contains(bloodGroup.toUpperCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "blood_group", bloodGroup, "INVALID_VALUE", "Blood group must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-.", "WARNING"));
            }

            // 6. B-Form / CNIC (optional)
            if (!isEmpty(bForm) && !CNIC_PATTERN.matcher(bForm.replace("-", "")).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "b_form_number", bForm, "INVALID_FORMAT", "B-Form must be 13 digits (e.g. 42101-1234567-3).", "WARNING"));
            }

            // 7. Residential address
            if (isEmpty(resAddress)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "residential_address", resAddress, "REQUIRED_FIELD", "Residential address is mandatory.", "ERROR"));
            }

            // 8. Admission date
            if (isEmpty(admDateStr)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "admission_date", admDateStr, "REQUIRED_FIELD", "Admission date is mandatory.", "ERROR"));
            } else if (parseDate(admDateStr) == null) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "admission_date", admDateStr, "INVALID_DATE_FORMAT", "Admission date must be in YYYY-MM-DD format.", "ERROR"));
            }

            // 9. Academic Session
            if (isEmpty(sessionName)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "academic_session", sessionName, "REQUIRED_FIELD", "Academic session is mandatory.", "ERROR"));
            } else if (!sessionMap.containsKey(sessionName.toLowerCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "academic_session", sessionName, "NOT_FOUND", "Academic session '" + sessionName + "' does not exist in school records.", "ERROR"));
            }

            // 10. Class and Section
            ClassEntity foundClass = null;
            if (isEmpty(className)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "class_name", className, "REQUIRED_FIELD", "Class name is mandatory.", "ERROR"));
            } else {
                foundClass = classMap.get(className.toLowerCase());
                if (foundClass == null) {
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "class_name", className, "NOT_FOUND", "Class '" + className + "' does not exist.", "ERROR"));
                }
            }

            if (isEmpty(sectionName)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "section_name", sectionName, "REQUIRED_FIELD", "Section name is mandatory.", "ERROR"));
            } else if (foundClass != null) {
                Optional<Section> secOpt = sectionRepository.findByClassEntityIdAndName(foundClass.getId(), sectionName);
                if (secOpt.isEmpty()) {
                    rowErrors.add(new DataImportError(job, rowNum, "Students", "section_name", sectionName, "NOT_FOUND", "Section '" + sectionName + "' not found under " + foundClass.getName() + ".", "ERROR"));
                }
            }

            // 11. Guardian Information
            if (isEmpty(guardianName)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "guardian_name", guardianName, "REQUIRED_FIELD", "Guardian full name is mandatory.", "ERROR"));
            }

            if (isEmpty(guardianRelation)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "guardian_relation", guardianRelation, "REQUIRED_FIELD", "Guardian relationship is mandatory.", "ERROR"));
            } else if (!VALID_RELATIONS.contains(guardianRelation.toUpperCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "guardian_relation", guardianRelation, "INVALID_VALUE", "Guardian relation must be FATHER, MOTHER, or GUARDIAN.", "WARNING"));
            }

            if (isEmpty(guardianPhone)) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "guardian_phone", guardianPhone, "REQUIRED_FIELD", "Guardian primary phone is mandatory.", "ERROR"));
            } else if (!PHONE_PATTERN.matcher(guardianPhone).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "guardian_phone", guardianPhone, "INVALID_PHONE", "Guardian phone must be a valid phone number (e.g. +92-300-1234567).", "ERROR"));
            }

            if (!isEmpty(guardianCnic) && !CNIC_PATTERN.matcher(guardianCnic.replace("-", "")).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Students", "guardian_cnic", guardianCnic, "INVALID_CNIC", "Guardian CNIC must be 13 digits (e.g. 42101-1234567-1).", "WARNING"));
            }

            // Determine Row Status
            boolean hasError = rowErrors.stream().anyMatch(e -> "ERROR".equalsIgnoreCase(e.getSeverity()));
            boolean hasWarning = rowErrors.stream().anyMatch(e -> "WARNING".equalsIgnoreCase(e.getSeverity()));

            String rowStatus;
            if (isDuplicate) {
                rowStatus = "DUPLICATE";
                duplicateRows++;
            } else if (hasError) {
                rowStatus = "INVALID";
                invalidRows++;
            } else if (hasWarning) {
                rowStatus = "WARNING";
                warningRows++;
            } else {
                rowStatus = "VALID";
                validRows++;
            }

            DataImportJobRow jobRow = new DataImportJobRow();
            jobRow.setJob(job);
            jobRow.setRowNumber(rowNum);
            jobRow.setSheetName("Students");
            jobRow.setEntityIdentifier(admissionNumber != null ? admissionNumber : "Row-" + rowNum);
            jobRow.setRowDataJson(toJson(row));
            jobRow.setStatus(rowStatus);
            if (!rowErrors.isEmpty()) {
                jobRow.setErrorMessage(rowErrors.get(0).getErrorMessage());
            }

            jobRowsToSave.add(jobRow);
            errorsToSave.addAll(rowErrors);
        }

        rowRepository.saveAll(jobRowsToSave);
        errorRepository.saveAll(errorsToSave);

        job.setTotalRows(totalRows);
        job.setValidRows(validRows);
        job.setInvalidRows(invalidRows);
        job.setDuplicateRows(duplicateRows);
        job.setWarningRows(warningRows);
    }

    // =========================================================================
    // STAFF VALIDATION LOGIC
    // =========================================================================

    private void validateStaff(DataImportJob job, List<Map<String, String>> rawRows) {
        Set<String> seenEmployeeIdsInFile = new HashSet<>();
        Set<String> seenEmailsInFile = new HashSet<>();
        int totalRows = rawRows.size();
        int validRows = 0;
        int invalidRows = 0;
        int duplicateRows = 0;
        int warningRows = 0;

        List<DataImportJobRow> jobRowsToSave = new ArrayList<>();
        List<DataImportError> errorsToSave = new ArrayList<>();

        for (int i = 0; i < rawRows.size(); i++) {
            int rowNum = i + 2;
            Map<String, String> row = rawRows.get(i);
            List<DataImportError> rowErrors = new ArrayList<>();

            String employeeId = sanitize(row.get("employee_id"));
            String fullName = sanitize(row.get("full_name"));
            String gender = sanitize(row.get("gender"));
            String designation = sanitize(row.get("designation"));
            String department = sanitize(row.get("department"));
            String category = sanitize(row.get("category"));
            String qualification = sanitize(row.get("qualification"));
            String joiningDateStr = sanitize(row.get("joining_date"));
            String officialEmail = sanitize(row.get("official_email"));
            String phoneNumber = sanitize(row.get("phone_number"));
            String cnic = sanitize(row.get("cnic"));
            String salaryStr = sanitize(row.get("basic_salary"));
            String status = sanitize(row.get("status"));
            String assignmentsStr = sanitize(row.get("teacher_subject_assignments"));

            boolean isDuplicate = false;

            // 1. Employee ID
            if (isEmpty(employeeId)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "employee_id", employeeId, "REQUIRED_FIELD", "Employee ID is mandatory.", "ERROR"));
            } else if (!IDENTIFIER_PATTERN.matcher(employeeId).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "employee_id", employeeId, "INVALID_FORMAT", "Employee ID must be alphanumeric (e.g. STF-2024-001).", "ERROR"));
            } else {
                String empUpper = employeeId.toUpperCase();
                if (seenEmployeeIdsInFile.contains(empUpper)) {
                    isDuplicate = true;
                    rowErrors.add(new DataImportError(job, rowNum, "Staff", "employee_id", employeeId, "DUPLICATE_IN_FILE", "Employee ID '" + employeeId + "' duplicates another row in this file.", "ERROR"));
                } else {
                    seenEmployeeIdsInFile.add(empUpper);
                }

                if (staffRepository.existsByEmployeeId(employeeId)) {
                    isDuplicate = true;
                    rowErrors.add(new DataImportError(job, rowNum, "Staff", "employee_id", employeeId, "DUPLICATE_IN_DB", "Employee ID '" + employeeId + "' already exists in staff directory.", "ERROR"));
                }
            }

            // 2. Full Name
            if (isEmpty(fullName)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "full_name", fullName, "REQUIRED_FIELD", "Full name is mandatory.", "ERROR"));
            } else if (fullName.length() > 128) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "full_name", fullName, "MAX_LENGTH_EXCEEDED", "Full name must not exceed 128 characters.", "ERROR"));
            }

            // 3. Gender
            if (isEmpty(gender)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "gender", gender, "REQUIRED_FIELD", "Gender is mandatory.", "ERROR"));
            } else if (!VALID_GENDERS.contains(gender.toUpperCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "gender", gender, "INVALID_VALUE", "Gender must be MALE, FEMALE, or OTHER.", "ERROR"));
            }

            // 4. Designation & Department
            if (isEmpty(designation)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "designation", designation, "REQUIRED_FIELD", "Designation is mandatory.", "ERROR"));
            }
            if (isEmpty(department)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "department", department, "REQUIRED_FIELD", "Department is mandatory.", "ERROR"));
            }

            // 5. Category
            if (isEmpty(category)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "category", category, "REQUIRED_FIELD", "Category is mandatory.", "ERROR"));
            } else if (!VALID_STAFF_CATEGORIES.contains(category.toUpperCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "category", category, "INVALID_VALUE", "Category must be TEACHING, NON_TEACHING, or ADMINISTRATIVE.", "ERROR"));
            }

            // 6. Joining Date
            if (isEmpty(joiningDateStr)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "joining_date", joiningDateStr, "REQUIRED_FIELD", "Joining date is mandatory.", "ERROR"));
            } else if (parseDate(joiningDateStr) == null) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "joining_date", joiningDateStr, "INVALID_DATE_FORMAT", "Joining date must be in YYYY-MM-DD format.", "ERROR"));
            }

            // 7. Official Email
            if (isEmpty(officialEmail)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "official_email", officialEmail, "REQUIRED_FIELD", "Official email is mandatory.", "ERROR"));
            } else if (!EMAIL_PATTERN.matcher(officialEmail).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "official_email", officialEmail, "INVALID_EMAIL", "Official email is invalid.", "ERROR"));
            } else {
                String emailLower = officialEmail.toLowerCase();
                if (seenEmailsInFile.contains(emailLower)) {
                    isDuplicate = true;
                    rowErrors.add(new DataImportError(job, rowNum, "Staff", "official_email", officialEmail, "DUPLICATE_IN_FILE", "Official email '" + officialEmail + "' duplicates another row in this file.", "ERROR"));
                } else {
                    seenEmailsInFile.add(emailLower);
                }

                if (userRepository.existsByEmail(officialEmail)) {
                    isDuplicate = true;
                    rowErrors.add(new DataImportError(job, rowNum, "Staff", "official_email", officialEmail, "DUPLICATE_IN_DB", "Official email '" + officialEmail + "' is already in use by a portal account.", "ERROR"));
                }
            }

            // 8. Phone Number
            if (isEmpty(phoneNumber)) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "phone_number", phoneNumber, "REQUIRED_FIELD", "Phone number is mandatory.", "ERROR"));
            } else if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "phone_number", phoneNumber, "INVALID_PHONE", "Phone number is invalid.", "ERROR"));
            }

            // 9. CNIC (optional)
            if (!isEmpty(cnic) && !CNIC_PATTERN.matcher(cnic.replace("-", "")).matches()) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "cnic", cnic, "INVALID_CNIC", "CNIC must be 13 digits (e.g. 42101-1234567-1).", "WARNING"));
            }

            // 10. Basic Salary (optional)
            if (!isEmpty(salaryStr)) {
                try {
                    double sal = Double.parseDouble(salaryStr.trim());
                    if (sal < 0) {
                        rowErrors.add(new DataImportError(job, rowNum, "Staff", "basic_salary", salaryStr, "NEGATIVE_VALUE", "Basic salary cannot be negative.", "ERROR"));
                    }
                } catch (NumberFormatException e) {
                    rowErrors.add(new DataImportError(job, rowNum, "Staff", "basic_salary", salaryStr, "INVALID_NUMBER", "Basic salary must be numeric.", "ERROR"));
                }
            }

            // 11. Status
            if (!isEmpty(status) && !VALID_STAFF_STATUSES.contains(status.toUpperCase())) {
                rowErrors.add(new DataImportError(job, rowNum, "Staff", "status", status, "INVALID_VALUE", "Status must be ACTIVE, PROBATION, SUSPENDED, or TERMINATED.", "ERROR"));
            }

            // 12. Teacher Assignments (optional)
            if (!isEmpty(assignmentsStr)) {
                if (!"TEACHING".equalsIgnoreCase(category)) {
                    rowErrors.add(new DataImportError(job, rowNum, "Staff", "teacher_subject_assignments", assignmentsStr, "INVALID_ASSIGNMENT", "Assignments specified for non-teaching staff member.", "WARNING"));
                } else {
                    String[] items = assignmentsStr.split(";");
                    for (String item : items) {
                        String[] parts = item.split("\\|");
                        if (parts.length < 3) {
                            rowErrors.add(new DataImportError(job, rowNum, "Staff", "teacher_subject_assignments", item, "INVALID_FORMAT", "Assignment format must be Class|Section|Subject.", "WARNING"));
                        }
                    }
                }
            }

            boolean hasError = rowErrors.stream().anyMatch(e -> "ERROR".equalsIgnoreCase(e.getSeverity()));
            boolean hasWarning = rowErrors.stream().anyMatch(e -> "WARNING".equalsIgnoreCase(e.getSeverity()));

            String rowStatus;
            if (isDuplicate) {
                rowStatus = "DUPLICATE";
                duplicateRows++;
            } else if (hasError) {
                rowStatus = "INVALID";
                invalidRows++;
            } else if (hasWarning) {
                rowStatus = "WARNING";
                warningRows++;
            } else {
                rowStatus = "VALID";
                validRows++;
            }

            DataImportJobRow jobRow = new DataImportJobRow();
            jobRow.setJob(job);
            jobRow.setRowNumber(rowNum);
            jobRow.setSheetName("Staff");
            jobRow.setEntityIdentifier(employeeId != null ? employeeId : "Row-" + rowNum);
            jobRow.setRowDataJson(toJson(row));
            jobRow.setStatus(rowStatus);
            if (!rowErrors.isEmpty()) {
                jobRow.setErrorMessage(rowErrors.get(0).getErrorMessage());
            }

            jobRowsToSave.add(jobRow);
            errorsToSave.addAll(rowErrors);
        }

        rowRepository.saveAll(jobRowsToSave);
        errorRepository.saveAll(errorsToSave);

        job.setTotalRows(totalRows);
        job.setValidRows(validRows);
        job.setInvalidRows(invalidRows);
        job.setDuplicateRows(duplicateRows);
        job.setWarningRows(warningRows);
    }

    // =========================================================================
    // 3. PREVIEW & REPORT QUERIES
    // =========================================================================

    public DataImportPreviewDto getJobPreview(String jobId, int page, int size, String statusFilter, String search) {
        DataImportJob job = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));

        List<DataImportJobRow> allRows = rowRepository.findByJobIdOrderByRowNumberAsc(job.getId());

        List<DataImportRowPreviewDto> rowDtos = new ArrayList<>();
        for (DataImportJobRow r : allRows) {
            if (statusFilter != null && !statusFilter.isBlank() && !"ALL".equalsIgnoreCase(statusFilter)) {
                if (!statusFilter.equalsIgnoreCase(r.getStatus())) {
                    continue;
                }
            }

            if (search != null && !search.isBlank()) {
                String s = search.toLowerCase();
                boolean matches = (r.getEntityIdentifier() != null && r.getEntityIdentifier().toLowerCase().contains(s)) ||
                        (r.getRowDataJson() != null && r.getRowDataJson().toLowerCase().contains(s)) ||
                        (r.getErrorMessage() != null && r.getErrorMessage().toLowerCase().contains(s));
                if (!matches) {
                    continue;
                }
            }

            Map<String, String> dataMap = fromJson(r.getRowDataJson());
            rowDtos.add(new DataImportRowPreviewDto(
                    r.getId(),
                    r.getRowNumber(),
                    r.getSheetName(),
                    r.getEntityIdentifier(),
                    dataMap,
                    r.getStatus(),
                    r.getErrorMessage()
            ));
        }

        int totalFiltered = rowDtos.size();
        int fromIndex = Math.min(page * size, totalFiltered);
        int toIndex = Math.min(fromIndex + size, totalFiltered);
        List<DataImportRowPreviewDto> paged = rowDtos.subList(fromIndex, toIndex);

        // Fetch top errors
        List<DataImportError> errors = errorRepository.findByJobIdOrderByRowNumberAsc(job.getId());
        List<DataImportErrorDto> errorDtos = errors.stream()
                .map(e -> new DataImportErrorDto(
                        e.getId(), e.getRowNumber(), e.getSheetName(), e.getFieldName(),
                        e.getFieldValue(), e.getErrorCode(), e.getErrorMessage(), e.getSeverity()
                ))
                .toList();

        return new DataImportPreviewDto(
                job.getJobId(),
                job.getImportType(),
                job.getStatus(),
                job.getOriginalFilename(),
                job.getFileFormat(),
                job.getFileSizeBytes(),
                job.getTotalRows(),
                job.getValidRows(),
                job.getInvalidRows(),
                job.getDuplicateRows(),
                job.getWarningRows(),
                job.getImportedRows(),
                job.getSkippedRows(),
                job.getImportMode(),
                paged,
                errorDtos,
                page,
                size,
                totalFiltered
        );
    }

    public List<DataImportErrorDto> getJobErrors(String jobId) {
        DataImportJob job = jobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Import job not found: " + jobId));
        return errorRepository.findByJobIdOrderByRowNumberAsc(job.getId()).stream()
                .map(e -> new DataImportErrorDto(
                        e.getId(), e.getRowNumber(), e.getSheetName(), e.getFieldName(),
                        e.getFieldValue(), e.getErrorCode(), e.getErrorMessage(), e.getSeverity()
                ))
                .toList();
    }

    // =========================================================================
    // PARSING HELPERS (Excel & CSV)
    // =========================================================================

    private List<Map<String, String>> parseExcel(File file) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (InputStream is = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(is)) {

            // Sheet 0 or first sheet with data
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return rows;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return rows;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                String val = getCellValueAsString(cell);
                headers.add(val != null ? val.trim().toLowerCase() : "");
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                boolean hasContent = false;
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int c = 0; c < headers.size(); c++) {
                    String h = headers.get(c);
                    if (h.isEmpty()) continue;
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String cellVal = getCellValueAsString(cell);
                    if (cellVal != null && !cellVal.trim().isEmpty()) {
                        hasContent = true;
                    }
                    rowMap.put(h, cellVal != null ? sanitize(cellVal) : "");
                }
                if (hasContent) {
                    rows.add(rowMap);
                }
            }
        }
        return rows;
    }

    private List<Map<String, String>> parseCsv(File file) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            Map<String, Integer> headerMap = parser.getHeaderMap();
            for (CSVRecord record : parser) {
                boolean hasContent = false;
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (String header : headerMap.keySet()) {
                    String val = record.isSet(header) ? record.get(header) : "";
                    if (val != null && !val.trim().isEmpty()) {
                        hasContent = true;
                    }
                    rowMap.put(header.trim().toLowerCase(), val != null ? sanitize(val) : "");
                }
                if (hasContent) {
                    rows.add(rowMap);
                }
            }
        }
        return rows;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDate ld = cell.getLocalDateTimeCellValue().toLocalDate();
                    yield ld.format(DateTimeFormatter.ISO_LOCAL_DATE);
                }
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    yield String.valueOf((long) num);
                }
                yield String.valueOf(num);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    /**
     * Sanitizes values against CSV/Excel Formula Injection.
     * Dangerous leading characters: =, +, -, @, tab, cr
     */
    private String sanitize(String val) {
        if (val == null) {
            return null;
        }
        String trimmed = val.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        char firstChar = trimmed.charAt(0);
        if (firstChar == '=' || firstChar == '+' || firstChar == '-' || firstChar == '@' || firstChar == '\t' || firstChar == '\r') {
            // Strip or safely neutralize formula trigger
            return trimmed.substring(1).trim();
        }
        return trimmed;
    }

    private boolean isEmpty(String val) {
        return val == null || val.trim().isEmpty();
    }

    private LocalDate parseDate(String val) {
        if (isEmpty(val)) {
            return null;
        }
        String clean = val.trim();
        try {
            return LocalDate.parse(clean, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(clean, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDate.parse(clean, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    private String toJson(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> fromJson(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
