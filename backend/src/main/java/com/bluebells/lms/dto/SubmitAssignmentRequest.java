package com.bluebells.lms.dto;

import jakarta.validation.constraints.NotNull;

public class SubmitAssignmentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String submissionText;
    private String fileUrl;

    public SubmitAssignmentRequest() {
    }

    public SubmitAssignmentRequest(Long studentId, String submissionText, String fileUrl) {
        this.studentId = studentId;
        this.submissionText = submissionText;
        this.fileUrl = fileUrl;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getSubmissionText() {
        return submissionText;
    }

    public void setSubmissionText(String submissionText) {
        this.submissionText = submissionText;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
