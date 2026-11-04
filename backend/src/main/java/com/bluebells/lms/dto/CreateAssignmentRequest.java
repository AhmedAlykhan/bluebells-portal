package com.bluebells.lms.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public class CreateAssignmentRequest {

    private Long moduleId;

    @NotBlank(message = "Assignment title is required")
    private String title;

    @NotBlank(message = "Instructions are required")
    private String instructions;

    @NotNull(message = "Maximum marks is required")
    @DecimalMin(value = "1.00", message = "Max marks must be at least 1.00")
    @DecimalMax(value = "1000.00", message = "Max marks cannot exceed 1000.00")
    private BigDecimal maxMarks = new BigDecimal("100.00");

    @DecimalMin(value = "0.00", message = "Passing marks cannot be negative")
    private BigDecimal passingMarks = new BigDecimal("40.00");

    @NotNull(message = "Due date is required")
    private Instant dueDate;

    private boolean allowLateSubmission = false;
    private String allowedFormats = "PDF,IMAGES,DOCX";
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSizeBytes;

    public CreateAssignmentRequest() {
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(BigDecimal maxMarks) {
        this.maxMarks = maxMarks;
    }

    public BigDecimal getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(BigDecimal passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isAllowLateSubmission() {
        return allowLateSubmission;
    }

    public void setAllowLateSubmission(boolean allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public String getAllowedFormats() {
        return allowedFormats;
    }

    public void setAllowedFormats(String allowedFormats) {
        this.allowedFormats = allowedFormats;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public Long getAttachmentSizeBytes() {
        return attachmentSizeBytes;
    }

    public void setAttachmentSizeBytes(Long attachmentSizeBytes) {
        this.attachmentSizeBytes = attachmentSizeBytes;
    }
}
