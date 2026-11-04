package com.bluebells.lms;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lms_submission_files")
public class SubmissionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private AssignmentSubmission submission;

    @Column(name = "file_path", nullable = false, length = 512)
    private String filePath;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "file_type", nullable = false, length = 64)
    private String fileType = "IMAGE";

    @Column(name = "upload_order", nullable = false)
    private int uploadOrder = 1;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public SubmissionFile() {
    }

    public SubmissionFile(AssignmentSubmission submission, String filePath, String originalFilename,
                          Long fileSizeBytes, String fileType, int uploadOrder) {
        this.submission = submission;
        this.filePath = filePath;
        this.originalFilename = originalFilename;
        this.fileSizeBytes = fileSizeBytes;
        this.fileType = fileType != null ? fileType : "IMAGE";
        this.uploadOrder = uploadOrder;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssignmentSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(AssignmentSubmission submission) {
        this.submission = submission;
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
