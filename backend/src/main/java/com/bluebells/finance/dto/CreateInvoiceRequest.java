package com.bluebells.finance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateInvoiceRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long sessionId;

    @NotBlank(message = "Billing month is required")
    private String billingMonth;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotEmpty(message = "Invoice items cannot be empty")
    @Valid
    private List<CreateInvoiceItemRequest> items;

    public CreateInvoiceRequest() {
    }

    public CreateInvoiceRequest(Long studentId, Long sessionId, String billingMonth, LocalDate dueDate, BigDecimal discountAmount, List<CreateInvoiceItemRequest> items) {
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.billingMonth = billingMonth;
        this.dueDate = dueDate;
        this.discountAmount = discountAmount;
        this.items = items;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public List<CreateInvoiceItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateInvoiceItemRequest> items) {
        this.items = items;
    }
}
