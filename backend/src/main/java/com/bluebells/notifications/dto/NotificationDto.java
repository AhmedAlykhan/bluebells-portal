package com.bluebells.notifications.dto;

import com.bluebells.notifications.Notification;
import java.time.Instant;

public class NotificationDto {

    private Long id;
    private Long recipientId;
    private String title;
    private String message;
    private String category;
    private boolean read;
    private Instant createdAt;

    public NotificationDto() {
    }

    public NotificationDto(Long id, Long recipientId, String title, String message, String category, boolean read, Instant createdAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.category = category;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static NotificationDto fromEntity(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getRecipient().getId(),
                n.getTitle(),
                n.getMessage(),
                n.getCategory(),
                n.isRead(),
                n.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
