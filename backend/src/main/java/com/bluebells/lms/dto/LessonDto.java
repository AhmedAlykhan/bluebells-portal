package com.bluebells.lms.dto;

import com.bluebells.lms.Lesson;
import java.time.Instant;

public class LessonDto {

    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private int sequenceOrder;
    private Instant publishedAt;

    public LessonDto() {
    }

    public static LessonDto fromEntity(Lesson entity) {
        if (entity == null) return null;
        LessonDto dto = new LessonDto();
        dto.setId(entity.getId());
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
        }
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setSequenceOrder(entity.getSequenceOrder());
        dto.setPublishedAt(entity.getPublishedAt());
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

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
