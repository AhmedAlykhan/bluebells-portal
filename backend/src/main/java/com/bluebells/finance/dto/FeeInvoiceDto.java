package com.bluebells.finance.dto;

import com.bluebells.finance.FeeInvoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeeInvoiceDto {

    private Long id;
    private String invoiceNumber;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long sessionId;
    private String sessionName;
    private String billingMonth;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private LocalDate dueDate;
    private String status;
    private List<FeeInvoiceItemDto> items = new ArrayList<>();

    public FeeInvoiceDto() {
    }

    public static FeeInvoiceDto fromEntity(FeeInvoice invoice) {
        FeeInvoiceDto dto = new FeeInvoiceDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setStudentId(invoice.getStudent().getId());
        dto.setStudentName(invoice.getStudent().getFullName());
        dto.setAdmissionNumber(invoice.getStudent().getAdmissionNumber());
        dto.setSessionId(invoice.getAcademicSession().getId());
        dto.setSessionName(invoice.getAcademicSession().getName());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setDiscountAmount(invoice.getDiscountAmount());
        dto.setPaidAmount(invoice.getPaidAmount());
        dto.setBalanceDue(invoice.getBalanceDue());
        dto.setDueDate(invoice.getDueDate());
        dto.setStatus(invoice.getStatus());
        if (invoice.getItems() != null) {
            dto.setItems(invoice.getItems().stream().map(FeeInvoiceItemDto::fromEntity).collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FeeInvoiceItemDto> getItems() {
        return items;
    }

    public void setItems(List<FeeInvoiceItemDto> items) {
        this.items = items;
    }
}
