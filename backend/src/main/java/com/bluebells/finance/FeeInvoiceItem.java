package com.bluebells.finance;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fee_invoice_items")
public class FeeInvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private FeeInvoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private FeeCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public FeeInvoiceItem() {
    }

    public FeeInvoiceItem(FeeInvoice invoice, FeeCategory category, BigDecimal amount) {
        this.invoice = invoice;
        this.category = category;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public FeeInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(FeeInvoice invoice) {
        this.invoice = invoice;
    }

    public FeeCategory getCategory() {
        return category;
    }

    public void setCategory(FeeCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
