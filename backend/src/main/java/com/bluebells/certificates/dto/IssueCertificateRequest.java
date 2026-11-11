package com.bluebells.certificates.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class IssueCertificateRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long templateId;

    private String certificateType; // MERIT, ACHIEVEMENT, PARTICIPATION, SPORTS, COMPLETION, CONDUCT

    private String customTitle;

    private String customNarrative;

    private String issuingAuthorityName;

    private String issuingAuthorityTitle;

    private LocalDate issueDate;

    public IssueCertificateRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }

    public String getCustomTitle() { return customTitle; }
    public void setCustomTitle(String customTitle) { this.customTitle = customTitle; }

    public String getCustomNarrative() { return customNarrative; }
    public void setCustomNarrative(String customNarrative) { this.customNarrative = customNarrative; }

    public String getIssuingAuthorityName() { return issuingAuthorityName; }
    public void setIssuingAuthorityName(String issuingAuthorityName) { this.issuingAuthorityName = issuingAuthorityName; }

    public String getIssuingAuthorityTitle() { return issuingAuthorityTitle; }
    public void setIssuingAuthorityTitle(String issuingAuthorityTitle) { this.issuingAuthorityTitle = issuingAuthorityTitle; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
}
