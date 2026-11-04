package com.bluebells.lms.dto;

import com.bluebells.lms.CourseModule;

public class CourseModuleDto {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String description;
    private int displayOrder;
    private int lessonCount;
    private int resourceCount;
    private int videoCount;
    private int assignmentCount;

    public CourseModuleDto() {
    }

    public static CourseModuleDto fromEntity(CourseModule entity) {
        if (entity == null) return null;
        CourseModuleDto dto = new CourseModuleDto();
        dto.setId(entity.getId());
        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
            dto.setCourseTitle(entity.getCourse().getTitle());
        }
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setLessonCount(entity.getLessons() != null ? entity.getLessons().size() : 0);
        dto.setResourceCount(entity.getResources() != null ? entity.getResources().size() : 0);
        dto.setVideoCount(entity.getVideos() != null ? entity.getVideos().size() : 0);
        dto.setAssignmentCount(entity.getAssignments() != null ? entity.getAssignments().size() : 0);
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

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
    }
}
