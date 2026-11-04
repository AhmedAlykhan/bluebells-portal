package com.bluebells.lms.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateLessonRequest {

    private Long moduleId;

    @NotBlank(message = "Lesson title is required")
    private String title;

    private String content;
    private int sequenceOrder = 0;
    private String lessonType = "LECTURE";
    private int durationMinutes = 0;

    public CreateLessonRequest() {
    }

    public CreateLessonRequest(String title, String content, int sequenceOrder) {
        this.title = title;
        this.content = content;
        this.sequenceOrder = sequenceOrder;
    }

    public CreateLessonRequest(Long moduleId, String title, String content, int sequenceOrder, String lessonType, int durationMinutes) {
        this.moduleId = moduleId;
        this.title = title;
        this.content = content;
        this.sequenceOrder = sequenceOrder;
        this.lessonType = lessonType != null ? lessonType : "LECTURE";
        this.durationMinutes = durationMinutes;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
