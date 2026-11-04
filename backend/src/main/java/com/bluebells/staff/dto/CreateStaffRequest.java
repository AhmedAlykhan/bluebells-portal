package com.bluebells.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateStaffRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Employee ID is required (e.g. EMP-2026-001)")
    @Size(max = 32, message = "Employee ID must not exceed 32 characters")
    private String employeeId;

    @NotBlank(message = "Designation is required")
    @Size(max = 64, message = "Designation must not exceed 64 characters")
    private String designation;

    @NotBlank(message = "Department is required")
    @Size(max = 64, message = "Department must not exceed 64 characters")
    private String department;

    private String qualification;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private BigDecimal salary;

    public CreateStaffRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
