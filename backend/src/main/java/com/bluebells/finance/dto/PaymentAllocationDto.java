package com.bluebells.finance.dto;

import com.bluebells.finance.PaymentAllocation;
import java.math.BigDecimal;

public class PaymentAllocationDto {

    private Long id;
    private Long invoiceId;
    private String invoiceNumber;
    private BigDecimal allocatedAmount;

    public PaymentAllocationDto() {
    }

    public PaymentAllocationDto(Long id, Long invoiceId, String invoiceNumber, BigDecimal allocatedAmount) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.allocatedAmount = allocatedAmount;
    }

    public static PaymentAllocationDto fromEntity(PaymentAllocation pa) {
        return new PaymentAllocationDto(
                pa.getId(),
                pa.getInvoice().getId(),
                pa.getInvoice().getInvoiceNumber(),
                pa.getAllocatedAmount()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}
