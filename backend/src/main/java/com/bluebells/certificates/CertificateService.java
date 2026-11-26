package com.bluebells.certificates;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.certificates.dto.*;
import com.bluebells.exception.ApiException;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLink;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CertificateService.class);

    private final CertificateTemplateRepository templateRepository;
    private final IssuedCertificateRepository certificateRepository;
    private final StudentRepository studentRepository;
    private final AcademicSessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository guardianLinkRepository;
    private final UserRepository userRepository;

    public CertificateService(
            CertificateTemplateRepository templateRepository,
            IssuedCertificateRepository certificateRepository,
            StudentRepository studentRepository,
            AcademicSessionRepository sessionRepository,
            EnrollmentRepository enrollmentRepository,
            GuardianRepository guardianRepository,
            GuardianStudentLinkRepository guardianLinkRepository,
            UserRepository userRepository) {
        this.templateRepository = templateRepository;
        this.certificateRepository = certificateRepository;
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.guardianRepository = guardianRepository;
        this.guardianLinkRepository = guardianLinkRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CertificateTemplateDto> getAllActiveTemplates() {
        return templateRepository.findByActiveTrue().stream()
                .map(this::mapTemplateToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificateTemplateDto saveTemplate(CertificateTemplateDto dto) {
        CertificateTemplate t;
        if (dto.getId() != null) {
            t = templateRepository.findById(dto.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Template not found with ID: " + dto.getId()));
        } else {
            t = new CertificateTemplate();
        }

        t.setTitle(dto.getTitle());
        t.setCertificateType(dto.getCertificateType().toUpperCase());
        t.setHeaderText(dto.getHeaderText());
        t.setBodyTemplate(dto.getBodyTemplate());
        if (dto.getDefaultSignatoryTitle() != null) t.setDefaultSignatoryTitle(dto.getDefaultSignatoryTitle());
        if (dto.getDefaultSignatoryName() != null) t.setDefaultSignatoryName(dto.getDefaultSignatoryName());
        if (dto.getBorderStyle() != null) t.setBorderStyle(dto.getBorderStyle());
        t.setActive(dto.isActive());

        return mapTemplateToDto(templateRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<IssuedCertificateDto> getCertificatesForStudent(Long studentId) {
        verifyReadAccessForStudent(studentId);
        return certificateRepository.findByStudentIdOrderByIssueDateDesc(studentId).stream()
                .map(this::mapCertificateToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssuedCertificateDto> getAllIssuedCertificates() {
        return certificateRepository.findAll().stream()
                .map(this::mapCertificateToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IssuedCertificateDto issueCertificate(IssueCertificateRequest req) {
        User currentUser = getCurrentUser();

        Student student = studentRepository.findById(req.getStudentId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Student not found with ID: " + req.getStudentId()));

        AcademicSession session = sessionRepository.findByCurrentTrue()
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No active academic session"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());
        if (enrollments.isEmpty() || enrollments.get(0).getSection() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot issue certificate: Student is not actively enrolled in any class or section");
        }
        String secName = enrollments.get(0).getSection().getName();
        String cName = enrollments.get(0).getSection().getClassEntity() != null
                ? enrollments.get(0).getSection().getClassEntity().getName()
                : "Active Class";

        List<GuardianStudentLink> links = guardianLinkRepository.findByStudentId(student.getId());
        String guardianName = (!links.isEmpty() && links.get(0).getGuardian() != null)
                ? links.get(0).getGuardian().getFullName()
                : null;
        if (guardianName == null || guardianName.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot issue certificate: Student has no registered guardian");
        }

        CertificateTemplate template = null;
        if (req.getTemplateId() != null) {
            template = templateRepository.findById(req.getTemplateId()).orElse(null);
        }

        String type = req.getCertificateType() != null ? req.getCertificateType().toUpperCase() :
                (template != null ? template.getCertificateType() : "MERIT");

        String title = req.getCustomTitle() != null ? req.getCustomTitle() :
                (template != null ? template.getTitle() : "Certificate of Academic Excellence");

        String narrative = req.getCustomNarrative();
        if ((narrative == null || narrative.trim().isEmpty()) && template != null) {
            String body = template.getBodyTemplate();
            String fullName = student.getFirstName() + " " + student.getLastName();
            narrative = body
                    .replace("{{STUDENT_NAME}}", fullName)
                    .replace("{student_name}", fullName)
                    .replace("{{student_name}}", fullName)
                    .replace("{{GUARDIAN_NAME}}", guardianName)
                    .replace("{guardian_name}", guardianName)
                    .replace("{{ADMISSION_NUMBER}}", student.getAdmissionNumber())
                    .replace("{admission_number}", student.getAdmissionNumber())
                    .replace("{{CLASS}}", cName)
                    .replace("{class_name}", cName)
                    .replace("{{SECTION}}", secName)
                    .replace("{section_name}", secName)
                    .replace("{{SESSION}}", session.getName())
                    .replace("{academic_session}", session.getName());
        }
        if (narrative == null || narrative.trim().isEmpty()) {
            narrative = "This is to certify that " + student.getFirstName() + " " + student.getLastName() +
                    " bearing Admission No. " + student.getAdmissionNumber() + " has achieved distinction.";
        }

        long seq = certificateRepository.count() + 1;
        String certNum;
        do {
            certNum = "BSS-CERT-" + LocalDate.now().getYear() + "-" + String.format("%04d", seq++);
        } while (certificateRepository.existsByCertificateNumber(certNum));

        String vCode = "VRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-BSS";

        IssuedCertificate cert = new IssuedCertificate();
        cert.setCertificateNumber(certNum);
        cert.setVerificationCode(vCode);
        cert.setTemplate(template);
        cert.setStudent(student);
        cert.setCertificateType(type);
        cert.setTitle(title);
        cert.setRecipientName(student.getFirstName() + " " + student.getLastName());
        cert.setAdmissionNumber(student.getAdmissionNumber());
        cert.setClassName(cName);
        cert.setSectionName(secName);
        cert.setAcademicSession(session.getName());
        cert.setNarrative(narrative);
        cert.setIssueDate(req.getIssueDate() != null ? req.getIssueDate() : LocalDate.now());
        cert.setIssuingAuthorityName(req.getIssuingAuthorityName() != null ? req.getIssuingAuthorityName() : (template != null ? template.getDefaultSignatoryName() : "Huma Arif"));
        cert.setIssuingAuthorityTitle(req.getIssuingAuthorityTitle() != null ? req.getIssuingAuthorityTitle() : (template != null ? template.getDefaultSignatoryTitle() : "Academic Principal"));
        cert.setIssuedBy(currentUser);
        cert.setStatus("ISSUED");

        IssuedCertificate saved = certificateRepository.save(cert);
        log.info("Certificate issued: certNumber={}, studentId={}, type={}, issuedBy={}",
                saved.getCertificateNumber(), student.getId(), saved.getCertificateType(), currentUser.getUsername());
        return mapCertificateToDto(saved);
    }

    @Transactional(readOnly = true)
    public CertificateVerificationResponse verifyCertificate(String code) {
        IssuedCertificate cert = certificateRepository.findByVerificationCode(code)
                .or(() -> certificateRepository.findByCertificateNumber(code))
                .orElse(null);

        if (cert == null) {
            return CertificateVerificationResponse.invalid("Invalid verification identifier. No matching certificate found in Bluebells registry.");
        }

        if (!"ISSUED".equalsIgnoreCase(cert.getStatus())) {
            return CertificateVerificationResponse.invalid("This certificate has been revoked (" + cert.getRevocationReason() + ").");
        }

        return CertificateVerificationResponse.verified(
                cert.getVerificationCode(),
                cert.getCertificateNumber(),
                cert.getCertificateType(),
                cert.getTitle(),
                cert.getRecipientName(),
                cert.getAdmissionNumber(),
                cert.getClassName(),
                cert.getAcademicSession(),
                cert.getIssueDate(),
                cert.getIssuingAuthorityName(),
                cert.getIssuingAuthorityTitle(),
                cert.getStatus()
        );
    }

    @Transactional
    public void revokeCertificate(Long id, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A formal revocation reason is mandatory to revoke an issued certificate.");
        }

        IssuedCertificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Certificate not found with ID: " + id));

        cert.setStatus("REVOKED");
        cert.setRevocationReason(reason.trim());
        certificateRepository.save(cert);
        log.warn("Certificate revoked: certNumber={}, reason={}", cert.getCertificateNumber(), reason.trim());
    }

    private void verifyReadAccessForStudent(Long studentId) {
        User currentUser = getCurrentUser();
        boolean isStaff = currentUser.getRoles().stream().anyMatch(r ->
                List.of("ROLE_SUPER_ADMIN", "ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL", "ROLE_TEACHER", "ROLE_ACCOUNTANT")
                        .contains(r.getName()));

        if (isStaff) return;

        boolean isStudent = currentUser.getRoles().stream().anyMatch(r -> "ROLE_STUDENT".equalsIgnoreCase(r.getName()));
        if (isStudent) {
            Student student = studentRepository.findByUserId(currentUser.getId()).orElse(null);
            if (student == null || !student.getId().equals(studentId)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You may only view your own certificates.");
            }
            return;
        }

        boolean isParent = currentUser.getRoles().stream().anyMatch(r -> "ROLE_PARENT".equalsIgnoreCase(r.getName()));
        if (isParent) {
            boolean isLinked = guardianLinkRepository.existsByGuardianUserIdAndStudentId(currentUser.getId(), studentId);
            if (!isLinked) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You can only view certificates for your registered children.");
            }
            return;
        }

        throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: Unauthorized role for certificates.");
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }
        String username = auth.getPrincipal() instanceof UserPrincipal ? ((UserPrincipal) auth.getPrincipal()).getUsername() : auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private CertificateTemplateDto mapTemplateToDto(CertificateTemplate t) {
        CertificateTemplateDto dto = new CertificateTemplateDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setCertificateType(t.getCertificateType());
        dto.setHeaderText(t.getHeaderText());
        dto.setBodyTemplate(t.getBodyTemplate());
        dto.setDefaultSignatoryTitle(t.getDefaultSignatoryTitle());
        dto.setDefaultSignatoryName(t.getDefaultSignatoryName());
        dto.setBorderStyle(t.getBorderStyle());
        dto.setActive(t.isActive());
        return dto;
    }

    private IssuedCertificateDto mapCertificateToDto(IssuedCertificate c) {
        IssuedCertificateDto dto = new IssuedCertificateDto();
        dto.setId(c.getId());
        dto.setCertificateNumber(c.getCertificateNumber());
        dto.setVerificationCode(c.getVerificationCode());
        dto.setTemplateId(c.getTemplate() != null ? c.getTemplate().getId() : null);
        dto.setStudentId(c.getStudent().getId());
        dto.setCertificateType(c.getCertificateType());
        dto.setTitle(c.getTitle());
        dto.setRecipientName(c.getRecipientName());
        dto.setAdmissionNumber(c.getAdmissionNumber());
        dto.setClassName(c.getClassName());
        dto.setSectionName(c.getSectionName());
        dto.setAcademicSession(c.getAcademicSession());
        dto.setNarrative(c.getNarrative());
        dto.setIssueDate(c.getIssueDate());
        dto.setIssuingAuthorityName(c.getIssuingAuthorityName());
        dto.setIssuingAuthorityTitle(c.getIssuingAuthorityTitle());
        dto.setIssuedById(c.getIssuedBy().getId());
        dto.setIssuedByName(c.getIssuedBy().getFullName());
        dto.setStatus(c.getStatus());
        dto.setBorderStyle(c.getTemplate() != null ? c.getTemplate().getBorderStyle() : "ROYAL_GOLD");
        dto.setVerificationUrl("https://portal.bluebells.edu.pk/verify-certificate?code=" + c.getVerificationCode());
        return dto;
    }
}
