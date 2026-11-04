package com.bluebells.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateFeeStructureRequest {

    private Long sessionId;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Fee category ID is required")
    private Long categoryId;

    @NotNull(message = "Fee amount is required")
    @DecimalMin(value = "0.01", message = "Fee amount must be greater than zero")
    private BigDecimal amount;

    private String frequency = "MONTHLY";

    public CreateFeeStructureRequest() {
    }

    public CreateFeeStructureRequest(Long sessionId, Long classId, Long categoryId, BigDecimal amount, String frequency) {
        this.sessionId = sessionId;
        this.classId = classId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.frequency = frequency;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
