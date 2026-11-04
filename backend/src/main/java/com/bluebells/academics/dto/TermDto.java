package com.bluebells.academics.dto;

import com.bluebells.academics.Term;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TermDto {

    private Long id;
    private Long sessionId;

    @NotBlank(message = "Term name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @DecimalMin(value = "0.00", message = "Weight percentage must be at least 0%")
    @DecimalMax(value = "100.00", message = "Weight percentage cannot exceed 100%")
    private BigDecimal weightPercentage;

    public TermDto() {
    }

    public TermDto(Long id, Long sessionId, String name, LocalDate startDate, LocalDate endDate, BigDecimal weightPercentage) {
        this.id = id;
        this.sessionId = sessionId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weightPercentage = weightPercentage;
    }

    public static TermDto fromEntity(Term entity) {
        if (entity == null) return null;
        return new TermDto(
                entity.getId(),
                entity.getAcademicSession() != null ? entity.getAcademicSession().getId() : null,
                entity.getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getWeightPercentage()
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(BigDecimal weightPercentage) {
        this.weightPercentage = weightPercentage;
    }
}
