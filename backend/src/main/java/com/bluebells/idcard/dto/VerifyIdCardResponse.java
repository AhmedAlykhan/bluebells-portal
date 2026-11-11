package com.bluebells.idcard.dto;

import java.time.LocalDate;

public class VerifyIdCardResponse {

    private boolean valid;
    private String cardNumber;
    private String studentName;
    private String admissionNumber;
    private String className;
    private String sessionName;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private String message;

    public VerifyIdCardResponse() {}

    public static VerifyIdCardResponse invalid(String message) {
        VerifyIdCardResponse r = new VerifyIdCardResponse();
        r.setValid(false);
        r.setMessage(message);
        return r;
    }

    public static VerifyIdCardResponse valid(String cardNumber, String studentName, String admissionNumber, String className, String sessionName, LocalDate issueDate, LocalDate expiryDate, String status) {
        VerifyIdCardResponse r = new VerifyIdCardResponse();
        r.setValid(true);
        r.setCardNumber(cardNumber);
        r.setStudentName(studentName);
        r.setAdmissionNumber(admissionNumber);
        r.setClassName(className);
        r.setSessionName(sessionName);
        r.setIssueDate(issueDate);
        r.setExpiryDate(expiryDate);
        r.setStatus(status);
        r.setMessage("Valid and verified Bluebells Schooling System Student ID Card");
        return r;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
