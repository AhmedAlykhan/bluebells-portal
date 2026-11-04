package com.bluebells.staff.dto;

import com.bluebells.staff.Staff;
import java.math.BigDecimal;
import java.time.LocalDate;

public class StaffDto {

    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String employeeId;
    private String designation;
    private String department;
    private String qualification;
    private LocalDate joiningDate;
    private BigDecimal salary;

    public StaffDto() {
    }

    public StaffDto(Long id, Long userId, String username, String fullName, String email, String employeeId, String designation, String department, String qualification, LocalDate joiningDate, BigDecimal salary) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.employeeId = employeeId;
        this.designation = designation;
        this.department = department;
        this.qualification = qualification;
        this.joiningDate = joiningDate;
        this.salary = salary;
    }

    public static StaffDto fromEntity(Staff entity) {
        if (entity == null) return null;
        return new StaffDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getUser() != null ? entity.getUser().getUsername() : null,
                entity.getUser() != null ? entity.getUser().getFullName() : null,
                entity.getUser() != null ? entity.getUser().getEmail() : null,
                entity.getEmployeeId(),
                entity.getDesignation(),
                entity.getDepartment(),
                entity.getQualification(),
                entity.getJoiningDate(),
                entity.getSalary()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
