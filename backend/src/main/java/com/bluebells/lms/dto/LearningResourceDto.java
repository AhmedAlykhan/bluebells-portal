package com.bluebells.lms.dto;

import com.bluebells.lms.LearningResource;
import java.time.Instant;

public class LearningResourceDto {

    private Long id;
    private Long courseId;
    private Long moduleId;
    private String moduleTitle;
    private Long lessonId;
    private String lessonTitle;
    private String title;
    private String description;
    private String resourceType;
    private String filePath;
    private String originalFilename;
    private Long fileSizeBytes;
    private String contentType;
    private String externalUrl;
    private boolean downloadable;
    private Long uploadedById;
    private String uploadedByName;
    private Instant createdAt;

    public LearningResourceDto() {
    }

    public static LearningResourceDto fromEntity(LearningResource entity) {
        if (entity == null) return null;
        LearningResourceDto dto = new LearningResourceDto();
        dto.setId(entity.getId());
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
        }
        if (entity.getModule() != null) {
            dto.setModuleId(entity.getModule().getId());
            dto.setModuleTitle(entity.getModule().getTitle());
        }
        if (entity.getLesson() != null) {
            dto.setLessonId(entity.getLesson().getId());
            dto.setLessonTitle(entity.getLesson().getTitle());
        }
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setResourceType(entity.getResourceType());
        dto.setFilePath(entity.getFilePath());
        dto.setOriginalFilename(entity.getOriginalFilename());
        dto.setFileSizeBytes(entity.getFileSizeBytes());
        dto.setContentType(entity.getContentType());
        dto.setExternalUrl(entity.getExternalUrl());
        dto.setDownloadable(entity.isDownloadable());
        if (entity.getUploadedBy() != null) {
            dto.setUploadedById(entity.getUploadedBy().getId());
            dto.setUploadedByName(entity.getUploadedBy().getFullName());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public Long getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(Long uploadedById) {
        this.uploadedById = uploadedById;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
