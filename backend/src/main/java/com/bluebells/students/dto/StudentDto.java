package com.bluebells.students.dto;

import com.bluebells.students.Enrollment;
import com.bluebells.students.Student;
import java.time.LocalDate;

public class StudentDto {

    private Long id;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String bFormNumber;
    private String residentialAddress;
    private LocalDate admissionDate;
    private String status;
    private String photoUrl;
    private String activeClassName;
    private String activeSectionName;
    private String rollNumber;

    public StudentDto() {
    }

    public static StudentDto fromEntity(Student entity) {
        if (entity == null) return null;
        StudentDto dto = new StudentDto();
        dto.setId(entity.getId());
        dto.setAdmissionNumber(entity.getAdmissionNumber());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setFullName(entity.getFullName());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());
        dto.setBloodGroup(entity.getBloodGroup());
        dto.setBFormNumber(entity.getBFormNumber());
        dto.setResidentialAddress(entity.getResidentialAddress());
        dto.setAdmissionDate(entity.getAdmissionDate());
        dto.setStatus(entity.getStatus());
        dto.setPhotoUrl(entity.getPhotoUrl());

        if (entity.getEnrollments() != null && !entity.getEnrollments().isEmpty()) {
            Enrollment active = entity.getEnrollments().stream()
                    .filter(e -> "ACTIVE".equalsIgnoreCase(e.getEnrollmentStatus()))
                    .findFirst()
                    .orElse(entity.getEnrollments().get(0));

            if (active.getSection() != null) {
                dto.setActiveSectionName(active.getSection().getName());
                if (active.getSection().getClassEntity() != null) {
                    dto.setActiveClassName(active.getSection().getClassEntity().getName());
                }
            }
            dto.setRollNumber(active.getRollNumber());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
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

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getActiveClassName() {
        return activeClassName;
    }

    public void setActiveClassName(String activeClassName) {
        this.activeClassName = activeClassName;
    }

    public String getActiveSectionName() {
        return activeSectionName;
    }

    public void setActiveSectionName(String activeSectionName) {
        this.activeSectionName = activeSectionName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
}
