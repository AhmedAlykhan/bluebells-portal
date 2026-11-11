package com.bluebells.support.dto;

import java.time.Instant;

public class SupportTicketMessageDto {

    private Long id;
    private Long ticketId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String message;
    private String attachmentUrl;
    private String attachmentName;
    private boolean internalNote;
    private Instant createdAt;

    public SupportTicketMessageDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }

    public boolean isInternalNote() { return internalNote; }
    public void setInternalNote(boolean internalNote) { this.internalNote = internalNote; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
