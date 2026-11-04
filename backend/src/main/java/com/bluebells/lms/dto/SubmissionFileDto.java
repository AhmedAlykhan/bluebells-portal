package com.bluebells.lms.dto;

import com.bluebells.lms.SubmissionFile;
import java.time.Instant;

public class SubmissionFileDto {

    private Long id;
    private Long submissionId;
    private String filePath;
    private String originalFilename;
    private Long fileSizeBytes;
    private String fileType;
    private int uploadOrder;
    private Instant createdAt;

    public SubmissionFileDto() {
    }

    public static SubmissionFileDto fromEntity(SubmissionFile entity) {
        if (entity == null) return null;
        SubmissionFileDto dto = new SubmissionFileDto();
        dto.setId(entity.getId());
        if (entity.getSubmission() != null) {
            dto.setSubmissionId(entity.getSubmission().getId());
        }
        dto.setFilePath(entity.getFilePath());
        dto.setOriginalFilename(entity.getOriginalFilename());
        dto.setFileSizeBytes(entity.getFileSizeBytes());
        dto.setFileType(entity.getFileType());
        dto.setUploadOrder(entity.getUploadOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getUploadOrder() {
        return uploadOrder;
    }

    public void setUploadOrder(int uploadOrder) {
        this.uploadOrder = uploadOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
