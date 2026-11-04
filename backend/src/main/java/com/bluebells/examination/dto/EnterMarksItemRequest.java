package com.bluebells.examination.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class EnterMarksItemRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.00", message = "Marks cannot be negative")
    private BigDecimal marksObtained;

    private BigDecimal maxMarks = new BigDecimal("100.00");
    private boolean absent = false;

    public EnterMarksItemRequest() {
    }

    public EnterMarksItemRequest(Long studentId, BigDecimal marksObtained, BigDecimal maxMarks, boolean absent) {
        this.studentId = studentId;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks != null ? maxMarks : new BigDecimal("100.00");
        this.absent = absent;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }
}
