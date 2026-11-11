package com.bluebells.support.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTicketRequest {

    private Long studentId; // Optional, if parent is reporting regarding a specific child

    @NotBlank(message = "Category is required")
    private String category; // ACADEMIC, TEACHER, FEE_QUERY, TRANSPORT, ADMINISTRATIVE, TECHNICAL_SUPPORT, SUGGESTION

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, URGENT

    private String assignedDepartment;

    public CreateTicketRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(String assignedDepartment) { this.assignedDepartment = assignedDepartment; }
}
