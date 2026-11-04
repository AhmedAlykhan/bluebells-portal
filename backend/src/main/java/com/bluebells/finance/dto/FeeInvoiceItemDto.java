package com.bluebells.finance.dto;

import com.bluebells.finance.FeeInvoiceItem;
import java.math.BigDecimal;

public class FeeInvoiceItemDto {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;

    public FeeInvoiceItemDto() {
    }

    public FeeInvoiceItemDto(Long id, Long categoryId, String categoryName, BigDecimal amount) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
    }

    public static FeeInvoiceItemDto fromEntity(FeeInvoiceItem item) {
        return new FeeInvoiceItemDto(
                item.getId(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getAmount()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
