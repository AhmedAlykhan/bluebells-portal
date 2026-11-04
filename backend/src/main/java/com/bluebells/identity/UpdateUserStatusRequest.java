package com.bluebells.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUserStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|SUSPENDED|PENDING_ACTIVATION", message = "Status must be ACTIVE, SUSPENDED, or PENDING_ACTIVATION")
    private String status;

    public UpdateUserStatusRequest() {
    }

    public UpdateUserStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
