package com.bluebells.certificates;

import com.bluebells.identity.User;
import com.bluebells.students.Student;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "issued_certificates")
public class IssuedCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_number", nullable = false, unique = true, length = 50)
    private String certificateNumber;

    @Column(name = "verification_code", nullable = false, unique = true, length = 64)
    private String verificationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private CertificateTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "certificate_type", nullable = false, length = 50)
    private String certificateType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "recipient_name", nullable = false, length = 255)
    private String recipientName;

    @Column(name = "admission_number", nullable = false, length = 50)
    private String admissionNumber;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "section_name", nullable = false, length = 50)
    private String sectionName;

    @Column(name = "academic_session", nullable = false, length = 100)
    private String academicSession;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String narrative;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "issuing_authority_name", nullable = false, length = 100)
    private String issuingAuthorityName = "Huma Arif";

    @Column(name = "issuing_authority_title", nullable = false, length = 100)
    private String issuingAuthorityTitle = "Academic Principal";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy;

    @Column(nullable = false, length = 50)
    private String status = "ISSUED"; // ISSUED, REVOKED

    @Column(name = "revocation_reason", length = 255)
    private String revocationReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public IssuedCertificate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public CertificateTemplate getTemplate() { return template; }
    public void setTemplate(CertificateTemplate template) { this.template = template; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getAcademicSession() { return academicSession; }
    public void setAcademicSession(String academicSession) { this.academicSession = academicSession; }

    public String getNarrative() { return narrative; }
    public void setNarrative(String narrative) { this.narrative = narrative; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getIssuingAuthorityName() { return issuingAuthorityName; }
    public void setIssuingAuthorityName(String issuingAuthorityName) { this.issuingAuthorityName = issuingAuthorityName; }

    public String getIssuingAuthorityTitle() { return issuingAuthorityTitle; }
    public void setIssuingAuthorityTitle(String issuingAuthorityTitle) { this.issuingAuthorityTitle = issuingAuthorityTitle; }

    public User getIssuedBy() { return issuedBy; }
    public void setIssuedBy(User issuedBy) { this.issuedBy = issuedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRevocationReason() { return revocationReason; }
    public void setRevocationReason(String revocationReason) { this.revocationReason = revocationReason; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
