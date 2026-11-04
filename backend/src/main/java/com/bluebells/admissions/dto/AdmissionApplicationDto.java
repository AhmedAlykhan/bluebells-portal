package com.bluebells.admissions.dto;

import com.bluebells.admissions.AdmissionApplication;
import java.time.Instant;
import java.time.LocalDate;

public class AdmissionApplicationDto {

    private Long id;
    private String applicationNumber;
    private String applicantName;
    private LocalDate dateOfBirth;
    private String gender;
    private Long appliedClassId;
    private String appliedClassName;
    private String guardianName;
    private String guardianPhone;
    private String guardianCnic;
    private String status;
    private String notes;
    private Instant createdAt;

    public AdmissionApplicationDto() {
    }

    public static AdmissionApplicationDto fromEntity(AdmissionApplication entity) {
        if (entity == null) return null;
        AdmissionApplicationDto dto = new AdmissionApplicationDto();
        dto.setId(entity.getId());
        dto.setApplicationNumber(entity.getApplicationNumber());
        dto.setApplicantName(entity.getApplicantName());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());
        dto.setAppliedClassId(entity.getAppliedClass() != null ? entity.getAppliedClass().getId() : null);
        dto.setAppliedClassName(entity.getAppliedClass() != null ? entity.getAppliedClass().getName() : null);
        dto.setGuardianName(entity.getGuardianName());
        dto.setGuardianPhone(entity.getGuardianPhone());
        dto.setGuardianCnic(entity.getGuardianCnic());
        dto.setStatus(entity.getStatus());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getAppliedClassId() {
        return appliedClassId;
    }

    public void setAppliedClassId(Long appliedClassId) {
        this.appliedClassId = appliedClassId;
    }

    public String getAppliedClassName() {
        return appliedClassName;
    }

    public void setAppliedClassName(String appliedClassName) {
        this.appliedClassName = appliedClassName;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public String getGuardianCnic() {
        return guardianCnic;
    }

    public void setGuardianCnic(String guardianCnic) {
        this.guardianCnic = guardianCnic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
