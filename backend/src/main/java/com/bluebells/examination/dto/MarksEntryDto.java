package com.bluebells.examination.dto;

import com.bluebells.examination.MarksEntry;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MarksEntryDto {

    private Long id;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private Long studentId;
    private String studentName;
    private BigDecimal marksObtained;
    private BigDecimal maxMarks;
    private BigDecimal percentage;
    private String grade;
    private boolean absent;
    private String status;
    private String recordedByName;

    public MarksEntryDto() {
    }

    public static MarksEntryDto fromEntity(MarksEntry entity) {
        if (entity == null) return null;
        MarksEntryDto dto = new MarksEntryDto();
        dto.setId(entity.getId());
        if (entity.getExam() != null) {
            dto.setExamId(entity.getExam().getId());
            dto.setExamName(entity.getExam().getName());
        }
        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getName());
        }
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getFullName());
        }
        dto.setMarksObtained(entity.getMarksObtained());
        dto.setMaxMarks(entity.getMaxMarks());

        if (entity.getMaxMarks() != null && entity.getMaxMarks().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = entity.getMarksObtained()
                    .divide(entity.getMaxMarks(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            dto.setPercentage(pct);
        }

        dto.setGrade(entity.getGrade());
        dto.setAbsent(entity.isAbsent());
        dto.setStatus(entity.getStatus());
        if (entity.getRecordedBy() != null) {
            dto.setRecordedByName(entity.getRecordedBy().getFullName());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(BigDecimal maxMarks) {
        this.maxMarks = maxMarks;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecordedByName() {
        return recordedByName;
    }

    public void setRecordedByName(String recordedByName) {
        this.recordedByName = recordedByName;
    }
}
