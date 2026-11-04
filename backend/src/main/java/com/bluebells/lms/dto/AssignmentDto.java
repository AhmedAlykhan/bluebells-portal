package com.bluebells.lms.dto;

import com.bluebells.lms.Assignment;
import java.math.BigDecimal;
import java.time.Instant;

public class AssignmentDto {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String instructions;
    private BigDecimal maxMarks;
    private Instant dueDate;
    private boolean allowLateSubmission;
    private String status;

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
        dto.setTitle(entity.getTitle());
        dto.setInstructions(entity.getInstructions());
        dto.setMaxMarks(entity.getMaxMarks());
        dto.setDueDate(entity.getDueDate());
        dto.setAllowLateSubmission(entity.isAllowLateSubmission());
        dto.setStatus(entity.getStatus());
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
