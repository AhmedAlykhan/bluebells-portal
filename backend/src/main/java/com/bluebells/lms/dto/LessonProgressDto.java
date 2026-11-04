package com.bluebells.lms.dto;

import com.bluebells.lms.LessonProgress;
import java.time.Instant;

public class LessonProgressDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long lessonId;
    private String lessonTitle;
    private Long courseId;
    private boolean completed;
    private Instant completedAt;
    private Instant lastAccessedAt;

    public LessonProgressDto() {
    }

    public static LessonProgressDto fromEntity(LessonProgress entity) {
        if (entity == null) return null;
        LessonProgressDto dto = new LessonProgressDto();
        dto.setId(entity.getId());
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getFullName());
        }
        if (entity.getLesson() != null) {
            dto.setLessonId(entity.getLesson().getId());
            dto.setLessonTitle(entity.getLesson().getTitle());
            if (entity.getLesson().getCourse() != null) {
                dto.setCourseId(entity.getLesson().getCourse().getId());
            }
        }
        dto.setCompleted(entity.isCompleted());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setLastAccessedAt(entity.getLastAccessedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}
