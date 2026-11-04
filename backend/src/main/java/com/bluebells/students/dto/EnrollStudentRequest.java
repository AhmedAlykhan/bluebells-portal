package com.bluebells.students.dto;

import jakarta.validation.constraints.NotNull;

public class EnrollStudentRequest {

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    private Long sessionId;

    private String rollNumber;

    public EnrollStudentRequest() {
    }

    public EnrollStudentRequest(Long sectionId, Long sessionId, String rollNumber) {
        this.sectionId = sectionId;
        this.sessionId = sessionId;
        this.rollNumber = rollNumber;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
}
