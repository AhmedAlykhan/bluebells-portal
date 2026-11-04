package com.bluebells.finance.dto;

import com.bluebells.finance.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentDto {

    private Long id;
    private String paymentReference;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receivedByName;
    private String notes;
    private String status;
    private List<PaymentAllocationDto> allocations = new ArrayList<>();

    public PaymentDto() {
    }

    public static PaymentDto fromEntity(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        dto.setStudentId(payment.getStudent().getId());
        dto.setStudentName(payment.getStudent().getFullName());
        dto.setAdmissionNumber(payment.getStudent().getAdmissionNumber());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setReceivedByName(payment.getReceivedBy() != null ? payment.getReceivedBy().getFullName() : null);
        dto.setNotes(payment.getNotes());
        dto.setStatus(payment.getStatus());
        if (payment.getAllocations() != null) {
            dto.setAllocations(payment.getAllocations().stream().map(PaymentAllocationDto::fromEntity).collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceivedByName() {
        return receivedByName;
    }

    public void setReceivedByName(String receivedByName) {
        this.receivedByName = receivedByName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PaymentAllocationDto> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<PaymentAllocationDto> allocations) {
        this.allocations = allocations;
    }
}
