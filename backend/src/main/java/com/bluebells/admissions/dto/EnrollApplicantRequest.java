package com.bluebells.admissions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class EnrollApplicantRequest {

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    private Long sessionId;

    private String rollNumber;

    @NotNull(message = "Admission date is required")
    private LocalDate admissionDate;

    private String bFormNumber;

    @NotBlank(message = "Residential address is required")
    private String residentialAddress;

    private String relationshipType = "FATHER";

    public EnrollApplicantRequest() {
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

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getBFormNumber() {
        return bFormNumber;
    }

    public void setBFormNumber(String bFormNumber) {
        this.bFormNumber = bFormNumber;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}
