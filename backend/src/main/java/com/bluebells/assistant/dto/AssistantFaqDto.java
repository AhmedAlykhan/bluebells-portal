package com.bluebells.assistant.dto;

import java.util.List;

public class AssistantFaqDto {

    private String category;
    private String title;
    private List<String> sampleQuestions;

    public AssistantFaqDto() {}

    public AssistantFaqDto(String category, String title, List<String> sampleQuestions) {
        this.category = category;
        this.title = title;
        this.sampleQuestions = sampleQuestions;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getSampleQuestions() { return sampleQuestions; }
    public void setSampleQuestions(List<String> sampleQuestions) { this.sampleQuestions = sampleQuestions; }
}
