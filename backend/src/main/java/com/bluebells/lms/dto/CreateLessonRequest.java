package com.bluebells.lms.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateLessonRequest {

    @NotBlank(message = "Lesson title is required")
    private String title;

    private String content;
    private int sequenceOrder = 0;

    public CreateLessonRequest() {
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
}
