package com.bluebells.lms.dto;

import com.bluebells.lms.YouTubeVideo;
import java.time.Instant;

public class YouTubeVideoDto {

    private Long id;
    private Long courseId;
    private Long moduleId;
    private String moduleTitle;
    private Long lessonId;
    private String lessonTitle;
    private String title;
    private String description;
    private String youtubeUrl;
    private String videoId;
    private int durationSeconds;
    private int displayOrder;
    private Instant createdAt;

    public YouTubeVideoDto() {
    }

    public static YouTubeVideoDto fromEntity(YouTubeVideo entity) {
        if (entity == null) return null;
        YouTubeVideoDto dto = new YouTubeVideoDto();
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
        dto.setYoutubeUrl(entity.getYoutubeUrl());
        dto.setVideoId(entity.getVideoId());
        dto.setDurationSeconds(entity.getDurationSeconds());
        dto.setDisplayOrder(entity.getDisplayOrder());
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

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
