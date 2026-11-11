package com.bluebells.support.dto;

import jakarta.validation.constraints.NotBlank;

public class ReplyTicketRequest {

    @NotBlank(message = "Reply message cannot be empty")
    private String message;

    private boolean internalNote = false;

    public ReplyTicketRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isInternalNote() { return internalNote; }
    public void setInternalNote(boolean internalNote) { this.internalNote = internalNote; }
}
