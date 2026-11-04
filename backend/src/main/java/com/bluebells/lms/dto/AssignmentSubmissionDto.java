package com.bluebells.lms.dto;

import com.bluebells.lms.AssignmentSubmission;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentSubmissionDto {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private BigDecimal maxMarks;
    private BigDecimal passingMarks;
    private String allowedFormats;
    private Long studentId;
    private String studentName;
    private String submissionText;
    private String fileUrl;
    private List<SubmissionFileDto> submissionFiles = new ArrayList<>();
    private Instant submittedAt;
    private boolean late;
    private String status;
    private BigDecimal marksObtained;
    private Boolean passed;
    private String teacherFeedback;
    private Instant gradedAt;
    private String gradedByName;

    public AssignmentSubmissionDto() {
    }

    public static AssignmentSubmissionDto fromEntity(AssignmentSubmission entity) {
        if (entity == null) return null;
        AssignmentSubmissionDto dto = new AssignmentSubmissionDto();
        dto.setId(entity.getId());
        if (entity.getAssignment() != null) {
            dto.setAssignmentId(entity.getAssignment().getId());
            dto.setAssignmentTitle(entity.getAssignment().getTitle());
            dto.setMaxMarks(entity.getAssignment().getMaxMarks());
            dto.setPassingMarks(entity.getAssignment().getPassingMarks());
            dto.setAllowedFormats(entity.getAssignment().getAllowedFormats());
        }
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getFullName());
        }
        dto.setSubmissionText(entity.getSubmissionText());
        dto.setFileUrl(entity.getFileUrl());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setLate(entity.isLate());
        dto.setStatus(entity.getStatus());
        dto.setMarksObtained(entity.getMarksObtained());

        if (entity.getMarksObtained() != null && entity.getAssignment() != null && entity.getAssignment().getPassingMarks() != null) {
            dto.setPassed(entity.getMarksObtained().compareTo(entity.getAssignment().getPassingMarks()) >= 0);
        }

        dto.setTeacherFeedback(entity.getTeacherFeedback());
        dto.setGradedAt(entity.getGradedAt());
        if (entity.getGradedBy() != null) {
            dto.setGradedByName(entity.getGradedBy().getFullName());
        }

        if (entity.getSubmissionFiles() != null) {
            dto.setSubmissionFiles(entity.getSubmissionFiles().stream()
                    .map(SubmissionFileDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(BigDecimal maxMarks) {
        this.maxMarks = maxMarks;
    }

    public BigDecimal getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(BigDecimal passingMarks) {
        this.passingMarks = passingMarks;
    }

    public String getAllowedFormats() {
        return allowedFormats;
    }

    public void setAllowedFormats(String allowedFormats) {
        this.allowedFormats = allowedFormats;
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

    public List<SubmissionFileDto> getSubmissionFiles() {
        return submissionFiles;
    }

    public void setSubmissionFiles(List<SubmissionFileDto> submissionFiles) {
        this.submissionFiles = submissionFiles;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isLate() {
        return late;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public Instant getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(Instant gradedAt) {
        this.gradedAt = gradedAt;
    }

    public String getGradedByName() {
        return gradedByName;
    }

    public void setGradedByName(String gradedByName) {
        this.gradedByName = gradedByName;
    }
}
