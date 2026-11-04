package com.bluebells.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AttendanceItemRequest {

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotBlank(message = "Status is required (PRESENT, ABSENT, LATE, EXCUSED)")
    private String status;

    private String remarks;

    public AttendanceItemRequest() {
    }

    public AttendanceItemRequest(Long enrollmentId, String status, String remarks) {
        this.enrollmentId = enrollmentId;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
