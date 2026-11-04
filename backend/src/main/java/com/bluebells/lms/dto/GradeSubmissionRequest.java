package com.bluebells.lms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class GradeSubmissionRequest {

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.00", message = "Marks obtained cannot be negative")
    private BigDecimal marksObtained;

    private String teacherFeedback;
    private String status = "GRADED"; // 'GRADED' or 'RETURNED_FOR_CORRECTION'

    public GradeSubmissionRequest() {
    }

    public GradeSubmissionRequest(BigDecimal marksObtained, String teacherFeedback) {
        this.marksObtained = marksObtained;
        this.teacherFeedback = teacherFeedback;
        this.status = "GRADED";
    }

    public GradeSubmissionRequest(BigDecimal marksObtained, String teacherFeedback, String status) {
        this.marksObtained = marksObtained;
        this.teacherFeedback = teacherFeedback;
        this.status = status != null ? status : "GRADED";
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
