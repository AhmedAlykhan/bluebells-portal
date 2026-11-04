package com.bluebells.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public class AttendanceCorrectionRequest {

    @NotBlank(message = "New status is required (PRESENT, ABSENT, LATE, EXCUSED)")
    private String newStatus;

    @NotBlank(message = "Reason for attendance correction is required")
    private String reason;

    public AttendanceCorrectionRequest() {
    }

    public AttendanceCorrectionRequest(String newStatus, String reason) {
        this.newStatus = newStatus;
        this.reason = reason;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
