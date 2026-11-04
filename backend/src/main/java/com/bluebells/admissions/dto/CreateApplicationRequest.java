package com.bluebells.admissions.dto;

import com.bluebells.admissions.AdmissionApplication;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateApplicationRequest {

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required (MALE, FEMALE, OTHER)")
    private String gender;

    @NotNull(message = "Target class is required")
    private Long appliedClassId;

    @NotBlank(message = "Guardian name is required")
    private String guardianName;

    @NotBlank(message = "Guardian primary phone is required")
    private String guardianPhone;

    private String guardianCnic;
    private String notes;

    public CreateApplicationRequest() {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
