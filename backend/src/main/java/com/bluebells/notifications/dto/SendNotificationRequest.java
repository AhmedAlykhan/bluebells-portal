package com.bluebells.notifications.dto;

import jakarta.validation.constraints.NotBlank;

public class SendNotificationRequest {

    private Long recipientId;
    private String targetRole;

    @NotBlank(message = "Notification title is required")
    private String title;

    @NotBlank(message = "Notification message is required")
    private String message;

    private String category = "SYSTEM";

    public SendNotificationRequest() {
    }

    public SendNotificationRequest(Long recipientId, String targetRole, String title, String message, String category) {
        this.recipientId = recipientId;
        this.targetRole = targetRole;
        this.title = title;
        this.message = message;
        this.category = category != null ? category : "SYSTEM";
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
