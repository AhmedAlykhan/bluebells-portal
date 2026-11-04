package com.bluebells.examination.dto;

import com.bluebells.examination.Exam;
import java.time.LocalDate;

public class ExamDto {

    private Long id;
    private Long sessionId;
    private String sessionName;
    private Long termId;
    private String termName;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public ExamDto() {
    }

    public static ExamDto fromEntity(Exam entity) {
        if (entity == null) return null;
        ExamDto dto = new ExamDto();
        dto.setId(entity.getId());
        if (entity.getAcademicSession() != null) {
            dto.setSessionId(entity.getAcademicSession().getId());
            dto.setSessionName(entity.getAcademicSession().getName());
        }
        if (entity.getTerm() != null) {
            dto.setTermId(entity.getTerm().getId());
            dto.setTermName(entity.getTerm().getName());
        }
        dto.setName(entity.getName());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        return dto;
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

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
