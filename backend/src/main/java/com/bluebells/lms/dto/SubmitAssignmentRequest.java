package com.bluebells.lms.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SubmitAssignmentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private String submissionText;
    private String fileUrl;

    private List<String> fileUrls = new ArrayList<>();
    private List<String> fileNames = new ArrayList<>();
    private List<Long> fileSizes = new ArrayList<>();
    private List<String> fileTypes = new ArrayList<>();

    public SubmitAssignmentRequest() {
    }

    public SubmitAssignmentRequest(Long studentId, String submissionText, String fileUrl) {
        this.studentId = studentId;
        this.submissionText = submissionText;
        this.fileUrl = fileUrl;
        if (fileUrl != null && !fileUrl.isBlank()) {
            this.fileUrls.add(fileUrl);
        }
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
        if (fileUrl != null && !fileUrl.isBlank() && !this.fileUrls.contains(fileUrl)) {
            this.fileUrls.add(fileUrl);
        }
    }

    public List<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(List<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public List<Long> getFileSizes() {
        return fileSizes;
    }

    public void setFileSizes(List<Long> fileSizes) {
        this.fileSizes = fileSizes;
    }

    public List<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }
}
