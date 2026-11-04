package com.bluebells.lms.dto;

import jakarta.validation.constraints.NotNull;

public class MarkLessonProgressRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private boolean completed = true;

    public MarkLessonProgressRequest() {
    }

    public MarkLessonProgressRequest(Long studentId, boolean completed) {
        this.studentId = studentId;
        this.completed = completed;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
