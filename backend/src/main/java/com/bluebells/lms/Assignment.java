package com.bluebells.lms;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private CourseModule module;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "max_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxMarks = new BigDecimal("100.00");

    @Column(name = "passing_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal passingMarks = new BigDecimal("40.00");

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "allow_late_submission", nullable = false)
    private boolean allowLateSubmission = false;

    @Column(name = "allowed_formats", nullable = false, length = 128)
    private String allowedFormats = "PDF,IMAGES,DOCX";

    @Column(name = "attachment_url", length = 512)
    private String attachmentUrl;

    @Column(name = "attachment_name", length = 255)
    private String attachmentName;

    @Column(name = "attachment_size_bytes")
    private Long attachmentSizeBytes;

    @Column(nullable = false, length = 16)
    private String status = "PUBLISHED";

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentSubmission> submissions = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Assignment() {
    }

    public Assignment(Course course, String title, String instructions, BigDecimal maxMarks, Instant dueDate, boolean allowLateSubmission) {
        this.course = course;
        this.title = title;
        this.instructions = instructions;
        this.maxMarks = maxMarks != null ? maxMarks : new BigDecimal("100.00");
        this.passingMarks = new BigDecimal("40.00");
        this.dueDate = dueDate;
        this.allowLateSubmission = allowLateSubmission;
        this.allowedFormats = "PDF,IMAGES,DOCX";
        this.status = "PUBLISHED";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Assignment(Course course, CourseModule module, String title, String instructions,
                      BigDecimal maxMarks, BigDecimal passingMarks, Instant dueDate,
                      boolean allowLateSubmission, String allowedFormats,
                      String attachmentUrl, String attachmentName, Long attachmentSizeBytes) {
        this.course = course;
        this.module = module;
        this.title = title;
        this.instructions = instructions;
        this.maxMarks = maxMarks != null ? maxMarks : new BigDecimal("100.00");
        this.passingMarks = passingMarks != null ? passingMarks : new BigDecimal("40.00");
        this.dueDate = dueDate;
        this.allowLateSubmission = allowLateSubmission;
        this.allowedFormats = allowedFormats != null ? allowedFormats : "PDF,IMAGES,DOCX";
        this.attachmentUrl = attachmentUrl;
        this.attachmentName = attachmentName;
        this.attachmentSizeBytes = attachmentSizeBytes;
        this.status = "PUBLISHED";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseModule getModule() {
        return module;
    }

    public void setModule(CourseModule module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isAllowLateSubmission() {
        return allowLateSubmission;
    }

    public void setAllowLateSubmission(boolean allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public String getAllowedFormats() {
        return allowedFormats;
    }

    public void setAllowedFormats(String allowedFormats) {
        this.allowedFormats = allowedFormats;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public Long getAttachmentSizeBytes() {
        return attachmentSizeBytes;
    }

    public void setAttachmentSizeBytes(Long attachmentSizeBytes) {
        this.attachmentSizeBytes = attachmentSizeBytes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AssignmentSubmission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<AssignmentSubmission> submissions) {
        this.submissions = submissions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
