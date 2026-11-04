package com.bluebells.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class GenerateMonthlyInvoicesRequest {

    private Long sessionId;
    private Long classId;

    @NotBlank(message = "Billing month is required (e.g. 2026-11)")
    private String billingMonth;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    public GenerateMonthlyInvoicesRequest() {
    }

    public GenerateMonthlyInvoicesRequest(Long sessionId, Long classId, String billingMonth, LocalDate dueDate) {
        this.sessionId = sessionId;
        this.classId = classId;
        this.billingMonth = billingMonth;
        this.dueDate = dueDate;
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

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
