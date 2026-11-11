package com.bluebells.support.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTicketStatusRequest {

    @NotBlank(message = "Status is required")
    private String status; // OPEN, IN_PROGRESS, AWAITING_RESPONSE, RESOLVED, CLOSED

    private String resolutionNotes;

    private String assignedDepartment;

    private Long assignedToId;

    public UpdateTicketStatusRequest() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(String assignedDepartment) { this.assignedDepartment = assignedDepartment; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
}
