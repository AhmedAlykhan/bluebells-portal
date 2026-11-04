package com.bluebells.finance;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_allocations")
public class PaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private FeeInvoice invoice;

    @Column(name = "allocated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal allocatedAmount;

    public PaymentAllocation() {
    }

    public PaymentAllocation(Payment payment, FeeInvoice invoice, BigDecimal allocatedAmount) {
        this.payment = payment;
        this.invoice = invoice;
        this.allocatedAmount = allocatedAmount;
    }

    public Long getId() {
        return id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public FeeInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(FeeInvoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}
