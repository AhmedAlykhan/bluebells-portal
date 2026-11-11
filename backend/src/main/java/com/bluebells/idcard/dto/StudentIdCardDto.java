package com.bluebells.idcard.dto;

import java.time.LocalDate;

public class StudentIdCardDto {

    private Long id;
    private Long studentId;
    private String admissionNumber;
    private String studentName;
    private String fatherName;
    private String className;
    private String sectionName;
    private String rollNumber;
    private String academicSession;
    private String bloodGroup;
    private String emergencyContact;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private int reissueCount;
    private String qrVerificationToken;
    private String qrPayload; // Safe verification URL (Never contains sensitive personal data)
    private String schoolAddress;
    private String schoolPhone;
    private String principalName;

    public StudentIdCardDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getAcademicSession() { return academicSession; }
    public void setAcademicSession(String academicSession) { this.academicSession = academicSession; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getReissueCount() { return reissueCount; }
    public void setReissueCount(int reissueCount) { this.reissueCount = reissueCount; }

    public String getQrVerificationToken() { return qrVerificationToken; }
    public void setQrVerificationToken(String qrVerificationToken) { this.qrVerificationToken = qrVerificationToken; }

    public String getQrPayload() { return qrPayload; }
    public void setQrPayload(String qrPayload) { this.qrPayload = qrPayload; }

    public String getSchoolAddress() { return schoolAddress; }
    public void setSchoolAddress(String schoolAddress) { this.schoolAddress = schoolAddress; }

    public String getSchoolPhone() { return schoolPhone; }
    public void setSchoolPhone(String schoolPhone) { this.schoolPhone = schoolPhone; }

    public String getPrincipalName() { return principalName; }
    public void setPrincipalName(String principalName) { this.principalName = principalName; }
}
