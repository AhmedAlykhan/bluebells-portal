package com.bluebells.assistant.dto;

import java.util.List;

public class AssistantQueryResponse {

    private String answer;
    private String category;
    private double confidence;
    private String sourceDocument;
    private List<String> suggestedFollowUps;
    private List<SuggestedActionDto> suggestedActions;
    private boolean automatedDisclaimer = true;
    private String disclaimer = "This is an automated virtual assistant guidance based on verified Bluebells Schooling System policy documentation. For binding institutional decisions, kindly contact the Principal's Office.";

    public AssistantQueryResponse() {}

    public AssistantQueryResponse(String answer, String category, double confidence, String sourceDocument, List<String> suggestedFollowUps, List<SuggestedActionDto> suggestedActions) {
        this.answer = answer;
        this.category = category;
        this.confidence = confidence;
        this.sourceDocument = sourceDocument;
        this.suggestedFollowUps = suggestedFollowUps;
        this.suggestedActions = suggestedActions;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }

    public List<String> getSuggestedFollowUps() { return suggestedFollowUps; }
    public void setSuggestedFollowUps(List<String> suggestedFollowUps) { this.suggestedFollowUps = suggestedFollowUps; }

    public List<SuggestedActionDto> getSuggestedActions() { return suggestedActions; }
    public void setSuggestedActions(List<SuggestedActionDto> suggestedActions) { this.suggestedActions = suggestedActions; }

    public boolean isAutomatedDisclaimer() { return automatedDisclaimer; }
    public void setAutomatedDisclaimer(boolean automatedDisclaimer) { this.automatedDisclaimer = automatedDisclaimer; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public static class SuggestedActionDto {
        private String label;
        private String path;

        public SuggestedActionDto() {}
        public SuggestedActionDto(String label, String path) {
            this.label = label;
            this.path = path;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}
