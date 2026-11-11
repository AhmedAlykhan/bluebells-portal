package com.bluebells.certificates.dto;

import java.time.LocalDate;

public class CertificateVerificationResponse {

    private boolean authentic;
    private String verificationCode;
    private String certificateNumber;
    private String certificateType;
    private String title;
    private String recipientName;
    private String admissionNumber;
    private String className;
    private String academicSession;
    private LocalDate issueDate;
    private String issuingAuthorityName;
    private String issuingAuthorityTitle;
    private String status;
    private String message;

    public CertificateVerificationResponse() {}

    public static CertificateVerificationResponse invalid(String message) {
        CertificateVerificationResponse r = new CertificateVerificationResponse();
        r.setAuthentic(false);
        r.setMessage(message);
        return r;
    }

    public static CertificateVerificationResponse verified(
            String verificationCode, String certificateNumber, String certificateType,
            String title, String recipientName, String admissionNumber, String className,
            String academicSession, LocalDate issueDate, String issuingAuthorityName,
            String issuingAuthorityTitle, String status) {
        CertificateVerificationResponse r = new CertificateVerificationResponse();
        r.setAuthentic(true);
        r.setVerificationCode(verificationCode);
        r.setCertificateNumber(certificateNumber);
        r.setCertificateType(certificateType);
        r.setTitle(title);
        r.setRecipientName(recipientName);
        r.setAdmissionNumber(admissionNumber);
        r.setClassName(className);
        r.setAcademicSession(academicSession);
        r.setIssueDate(issueDate);
        r.setIssuingAuthorityName(issuingAuthorityName);
        r.setIssuingAuthorityTitle(issuingAuthorityTitle);
        r.setStatus(status);
        r.setMessage("Officially Verified Bluebells Schooling System Digital Certificate");
        return r;
    }

    public boolean isAuthentic() { return authentic; }
    public void setAuthentic(boolean authentic) { this.authentic = authentic; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

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

    public String getAcademicSession() { return academicSession; }
    public void setAcademicSession(String academicSession) { this.academicSession = academicSession; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getIssuingAuthorityName() { return issuingAuthorityName; }
    public void setIssuingAuthorityName(String issuingAuthorityName) { this.issuingAuthorityName = issuingAuthorityName; }

    public String getIssuingAuthorityTitle() { return issuingAuthorityTitle; }
    public void setIssuingAuthorityTitle(String issuingAuthorityTitle) { this.issuingAuthorityTitle = issuingAuthorityTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
