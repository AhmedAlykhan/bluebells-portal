package com.bluebells.academics.dto;

import com.bluebells.academics.AcademicSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AcademicSessionDto {

    private Long id;

    @NotBlank(message = "Session name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private boolean current;
    private List<TermDto> terms;

    public AcademicSessionDto() {
    }

    public AcademicSessionDto(Long id, String name, LocalDate startDate, LocalDate endDate, boolean current, List<TermDto> terms) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.terms = terms;
    }

    public static AcademicSessionDto fromEntity(AcademicSession entity) {
        if (entity == null) return null;
        List<TermDto> termDtos = entity.getTerms() != null
                ? entity.getTerms().stream().map(TermDto::fromEntity).collect(Collectors.toList())
                : List.of();

        return new AcademicSessionDto(
                entity.getId(),
                entity.getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isCurrent(),
                termDtos
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public List<TermDto> getTerms() {
        return terms;
    }

    public void setTerms(List<TermDto> terms) {
        this.terms = terms;
    }
}
