package com.bluebells.lms.dto;

import com.bluebells.lms.Lesson;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LessonDto {

    private Long id;
    private Long courseId;
    private Long moduleId;
    private String moduleTitle;
    private String title;
    private String content;
    private int sequenceOrder;
    private String lessonType = "LECTURE";
    private int durationMinutes = 0;
    private boolean completed = false;
    private Instant publishedAt;
    private List<LearningResourceDto> resources = new ArrayList<>();
    private List<YouTubeVideoDto> videos = new ArrayList<>();

    public LessonDto() {
    }

    public static LessonDto fromEntity(Lesson entity) {
        if (entity == null) return null;
        LessonDto dto = new LessonDto();
        dto.setId(entity.getId());
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
        }
        if (entity.getModule() != null) {
            dto.setModuleId(entity.getModule().getId());
            dto.setModuleTitle(entity.getModule().getTitle());
        }
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setSequenceOrder(entity.getSequenceOrder());
        dto.setLessonType(entity.getLessonType() != null ? entity.getLessonType() : "LECTURE");
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setPublishedAt(entity.getPublishedAt());

        if (entity.getResources() != null) {
            dto.setResources(entity.getResources().stream()
                    .map(LearningResourceDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        if (entity.getVideos() != null) {
            dto.setVideos(entity.getVideos().stream()
                    .map(YouTubeVideoDto::fromEntity)
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<LearningResourceDto> getResources() {
        return resources;
    }

    public void setResources(List<LearningResourceDto> resources) {
        this.resources = resources;
    }

    public List<YouTubeVideoDto> getVideos() {
        return videos;
    }

    public void setVideos(List<YouTubeVideoDto> videos) {
        this.videos = videos;
    }
}
