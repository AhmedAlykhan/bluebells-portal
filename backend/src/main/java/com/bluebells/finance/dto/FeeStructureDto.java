package com.bluebells.finance.dto;

import com.bluebells.finance.FeeStructure;
import java.math.BigDecimal;

public class FeeStructureDto {

    private Long id;
    private Long sessionId;
    private String sessionName;
    private Long classId;
    private String className;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String frequency;

    public FeeStructureDto() {
    }

    public FeeStructureDto(Long id, Long sessionId, String sessionName, Long classId, String className,
                           Long categoryId, String categoryName, BigDecimal amount, String frequency) {
        this.id = id;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.classId = classId;
        this.className = className;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.frequency = frequency;
    }

    public static FeeStructureDto fromEntity(FeeStructure fs) {
        return new FeeStructureDto(
                fs.getId(),
                fs.getAcademicSession().getId(),
                fs.getAcademicSession().getName(),
                fs.getClassEntity().getId(),
                fs.getClassEntity().getName(),
                fs.getCategory().getId(),
                fs.getCategory().getName(),
                fs.getAmount(),
                fs.getFrequency()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
