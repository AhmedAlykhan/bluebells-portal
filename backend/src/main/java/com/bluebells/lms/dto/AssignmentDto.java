package com.bluebells.lms.dto;

import com.bluebells.lms.Assignment;
import java.math.BigDecimal;
import java.time.Instant;

public class AssignmentDto {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private Long moduleId;
    private String moduleTitle;
    private String title;
    private String instructions;
    private BigDecimal maxMarks;
    private BigDecimal passingMarks;
    private Instant dueDate;
    private boolean allowLateSubmission;
    private String allowedFormats;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSizeBytes;
    private String status;
    private int submissionCount;
    private int gradedCount;
    private AssignmentSubmissionDto studentSubmission;

    public AssignmentDto() {
    }

    public static AssignmentDto fromEntity(Assignment entity) {
        if (entity == null) return null;
        AssignmentDto dto = new AssignmentDto();
        dto.setId(entity.getId());
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
            dto.setCourseTitle(entity.getCourse().getTitle());
        }
        if (entity.getModule() != null) {
            dto.setModuleId(entity.getModule().getId());
            dto.setModuleTitle(entity.getModule().getTitle());
        }
        dto.setTitle(entity.getTitle());
        dto.setInstructions(entity.getInstructions());
        dto.setMaxMarks(entity.getMaxMarks());
        dto.setPassingMarks(entity.getPassingMarks());
        dto.setDueDate(entity.getDueDate());
        dto.setAllowLateSubmission(entity.isAllowLateSubmission());
        dto.setAllowedFormats(entity.getAllowedFormats());
        dto.setAttachmentUrl(entity.getAttachmentUrl());
        dto.setAttachmentName(entity.getAttachmentName());
        dto.setAttachmentSizeBytes(entity.getAttachmentSizeBytes());
        dto.setStatus(entity.getStatus());

        if (entity.getSubmissions() != null) {
            dto.setSubmissionCount(entity.getSubmissions().size());
            dto.setGradedCount((int) entity.getSubmissions().stream()
                    .filter(s -> "GRADED".equalsIgnoreCase(s.getStatus()))
                    .count());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }

    public int getGradedCount() {
        return gradedCount;
    }

    public void setGradedCount(int gradedCount) {
        this.gradedCount = gradedCount;
    }

    public AssignmentSubmissionDto getStudentSubmission() {
        return studentSubmission;
    }

    public void setStudentSubmission(AssignmentSubmissionDto studentSubmission) {
        this.studentSubmission = studentSubmission;
    }
}
