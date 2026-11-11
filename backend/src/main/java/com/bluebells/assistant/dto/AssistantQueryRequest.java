package com.bluebells.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public class AssistantQueryRequest {

    @NotBlank(message = "Query question cannot be blank")
    private String question;

    private String context; // e.g. "PORTAL_HELP", "FEES", "ADMISSIONS", "TIMINGS", "GENERAL"

    public AssistantQueryRequest() {}

    public AssistantQueryRequest(String question) {
        this.question = question;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
}
