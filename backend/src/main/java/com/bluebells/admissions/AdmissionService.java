package com.bluebells.admissions;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.admissions.dto.AdmissionApplicationDto;
import com.bluebells.admissions.dto.CreateApplicationRequest;
import com.bluebells.admissions.dto.EnrollApplicantRequest;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLink;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.bluebells.students.dto.StudentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@Service
public class AdmissionService {

    private final AdmissionApplicationRepository applicationRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final AcademicSessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository guardianStudentLinkRepository;

    public AdmissionService(
            AdmissionApplicationRepository applicationRepository,
            ClassRepository classRepository,
            SectionRepository sectionRepository,
            AcademicSessionRepository sessionRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            GuardianRepository guardianRepository,
            GuardianStudentLinkRepository guardianStudentLinkRepository) {
        this.applicationRepository = applicationRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.guardianRepository = guardianRepository;
        this.guardianStudentLinkRepository = guardianStudentLinkRepository;
    }

    @Transactional
    public AdmissionApplicationDto createApplication(CreateApplicationRequest request) {
        ClassEntity appliedClass = classRepository.findById(request.getAppliedClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + request.getAppliedClassId()));

        AdmissionApplication app = new AdmissionApplication();
        app.setApplicantName(request.getApplicantName());
        app.setDateOfBirth(request.getDateOfBirth());
        app.setGender(request.getGender().toUpperCase());
        app.setAppliedClass(appliedClass);
        app.setGuardianName(request.getGuardianName());
        app.setGuardianPhone(request.getGuardianPhone());
        app.setGuardianCnic(request.getGuardianCnic());
        app.setNotes(request.getNotes());
        app.setStatus("SUBMITTED");

        // Generate application number
        int year = Year.now().getValue();
        long count = applicationRepository.count() + 1;
        String appNumber = String.format("APP-%d-%04d", year, count);
        while (applicationRepository.existsByApplicationNumber(appNumber)) {
            count++;
            appNumber = String.format("APP-%d-%04d", year, count);
        }
        app.setApplicationNumber(appNumber);

        AdmissionApplication saved = applicationRepository.save(app);
        return AdmissionApplicationDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Page<AdmissionApplicationDto> getApplications(String status, Pageable pageable) {
        Page<AdmissionApplication> page;
        if (status != null && !status.trim().isEmpty()) {
            page = applicationRepository.findByStatus(status.trim().toUpperCase(), pageable);
        } else {
            page = applicationRepository.findAll(pageable);
        }
        return page.map(AdmissionApplicationDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public AdmissionApplicationDto getApplicationById(Long id) {
        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with id: " + id));
        return AdmissionApplicationDto.fromEntity(app);
    }

    @Transactional
    public AdmissionApplicationDto updateStatus(Long id, String status, String notes) {
        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with id: " + id));

        app.setStatus(status.toUpperCase());
        if (notes != null) {
            app.setNotes(notes);
        }
        AdmissionApplication updated = applicationRepository.save(app);
        return AdmissionApplicationDto.fromEntity(updated);
    }

    @Transactional
    public StudentDto enrollApplicant(Long applicationId, EnrollApplicantRequest request) {
        AdmissionApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission application not found with id: " + applicationId));

        if ("ENROLLED".equalsIgnoreCase(app.getStatus())) {
            throw new ApiException(HttpStatus.CONFLICT, "Applicant is already enrolled");
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + request.getSectionId()));

        AcademicSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));
        } else {
            session = sessionRepository.findByIsCurrentTrue()
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session found"));
        }

        // Generate student admission number
        int currentYear = Year.now().getValue();
        long studentCount = studentRepository.count() + 1;
        String admissionNumber = String.format("BSS-%d-%04d", currentYear, studentCount);
        while (studentRepository.existsByAdmissionNumber(admissionNumber)) {
            studentCount++;
            admissionNumber = String.format("BSS-%d-%04d", currentYear, studentCount);
        }

        // Split applicant name
        String fullName = app.getApplicantName().trim();
        String firstName = fullName;
        String lastName = "";
        int lastSpace = fullName.lastIndexOf(' ');
        if (lastSpace > 0) {
            firstName = fullName.substring(0, lastSpace);
            lastName = fullName.substring(lastSpace + 1);
        }

        Student student = new Student();
        student.setAdmissionNumber(admissionNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(app.getDateOfBirth());
        student.setGender(app.getGender());
        student.setBFormNumber(request.getBFormNumber());
        student.setResidentialAddress(request.getResidentialAddress());
        student.setAdmissionDate(request.getAdmissionDate() != null ? request.getAdmissionDate() : LocalDate.now());
        student.setStatus("ACTIVE");

        Student savedStudent = studentRepository.save(student);

        // Find or create Guardian
        Guardian guardian = null;
        if (app.getGuardianPhone() != null && !app.getGuardianPhone().trim().isEmpty()) {
            Optional<Guardian> existing = guardianRepository.findByPhonePrimary(app.getGuardianPhone().trim());
            if (existing.isPresent()) {
                guardian = existing.get();
            }
        }
        if (guardian == null && app.getGuardianCnic() != null && !app.getGuardianCnic().trim().isEmpty()) {
            Optional<Guardian> existing = guardianRepository.findByCnicNumber(app.getGuardianCnic().trim());
            if (existing.isPresent()) {
                guardian = existing.get();
            }
        }
        if (guardian == null) {
            guardian = new Guardian();
            guardian.setFullName(app.getGuardianName());
            guardian.setRelationshipType(request.getRelationshipType() != null ? request.getRelationshipType().toUpperCase() : "FATHER");
            guardian.setPhonePrimary(app.getGuardianPhone());
            guardian.setCnicNumber(app.getGuardianCnic());
            guardian.setAddress(request.getResidentialAddress());
            guardian = guardianRepository.save(guardian);
        }

        // Link Guardian and Student
        GuardianStudentLink link = new GuardianStudentLink(guardian, savedStudent, true, true);
        guardianStudentLinkRepository.save(link);

        // Create Enrollment
        Enrollment enrollment = new Enrollment(
                savedStudent,
                session,
                section,
                request.getRollNumber(),
                request.getAdmissionDate() != null ? request.getAdmissionDate() : LocalDate.now()
        );
        enrollmentRepository.save(enrollment);

        savedStudent.getEnrollments().add(enrollment);

        // Update application status
        app.setStatus("ENROLLED");
        applicationRepository.save(app);

        return StudentDto.fromEntity(savedStudent);
    }
}
