package com.bluebells.staff.dto;

import jakarta.validation.constraints.NotNull;

public class CreateTeacherAssignmentRequest {

    @NotNull(message = "Teacher is required")
    private Long teacherId;

    @NotNull(message = "Section is required")
    private Long sectionId;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    private Long sessionId;

    private boolean classTeacher = false;

    public CreateTeacherAssignmentRequest() {
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(boolean classTeacher) {
        this.classTeacher = classTeacher;
    }
}
