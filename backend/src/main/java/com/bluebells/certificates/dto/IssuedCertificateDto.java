package com.bluebells.certificates.dto;

import java.time.LocalDate;

public class IssuedCertificateDto {

    private Long id;
    private String certificateNumber;
    private String verificationCode;
    private Long templateId;
    private Long studentId;
    private String certificateType;
    private String title;
    private String recipientName;
    private String admissionNumber;
    private String className;
    private String sectionName;
    private String academicSession;
    private String narrative;
    private LocalDate issueDate;
    private String issuingAuthorityName;
    private String issuingAuthorityTitle;
    private Long issuedById;
    private String issuedByName;
    private String status;
    private String borderStyle;
    private String verificationUrl;

    public IssuedCertificateDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

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

    public Long getIssuedById() { return issuedById; }
    public void setIssuedById(Long issuedById) { this.issuedById = issuedById; }

    public String getIssuedByName() { return issuedByName; }
    public void setIssuedByName(String issuedByName) { this.issuedByName = issuedByName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBorderStyle() { return borderStyle; }
    public void setBorderStyle(String borderStyle) { this.borderStyle = borderStyle; }

    public String getVerificationUrl() { return verificationUrl; }
    public void setVerificationUrl(String verificationUrl) { this.verificationUrl = verificationUrl; }
}
