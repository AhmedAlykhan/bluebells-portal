package com.bluebells.idcard.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ReissueIdCardRequest {

    @NotBlank(message = "Reissue reason is required")
    private String reason; // "LOST", "DAMAGED", "PROMOTED", "DETAILS_UPDATED"

    public ReissueIdCardRequest() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
