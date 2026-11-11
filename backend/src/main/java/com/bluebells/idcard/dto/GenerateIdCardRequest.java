package com.bluebells.idcard.dto;

import jakarta.validation.constraints.NotNull;

public class GenerateIdCardRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long academicSessionId;

    public GenerateIdCardRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getAcademicSessionId() { return academicSessionId; }
    public void setAcademicSessionId(Long academicSessionId) { this.academicSessionId = academicSessionId; }
}
