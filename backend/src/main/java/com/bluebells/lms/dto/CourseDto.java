package com.bluebells.lms.dto;

import com.bluebells.lms.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDto {

    private Long id;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long sectionId;
    private String sectionName;
    private String className;
    private Long teacherId;
    private String teacherName;
    private String title;
    private String description;
    private String status;
    private int moduleCount;
    private int lessonCount;
    private int assignmentCount;
    private int resourceCount;
    private int videoCount;
    private Double completionPercentage;
    private List<CourseModuleDto> modules = new ArrayList<>();

    public CourseDto() {
    }

    public static CourseDto fromEntity(Course entity) {
        if (entity == null) return null;
        CourseDto dto = new CourseDto();
        dto.setId(entity.getId());
        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getName());
            dto.setSubjectCode(entity.getSubject().getCode());
        }
        if (entity.getSection() != null) {
            dto.setSectionId(entity.getSection().getId());
            dto.setSectionName(entity.getSection().getName());
            if (entity.getSection().getClassEntity() != null) {
                dto.setClassName(entity.getSection().getClassEntity().getName());
            }
        }
        if (entity.getTeacher() != null) {
            dto.setTeacherId(entity.getTeacher().getId());
            dto.setTeacherName(entity.getTeacher().getFullName());
        }
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());

        if (entity.getModules() != null) {
            dto.setModules(entity.getModules().stream()
                    .map(CourseModuleDto::fromEntity)
                    .collect(Collectors.toList()));
            dto.setModuleCount(entity.getModules().size());
        }
        dto.setLessonCount(entity.getLessons() != null ? entity.getLessons().size() : 0);
        dto.setAssignmentCount(entity.getAssignments() != null ? entity.getAssignments().size() : 0);
        dto.setResourceCount(entity.getResources() != null ? entity.getResources().size() : 0);
        dto.setVideoCount(entity.getVideos() != null ? entity.getVideos().size() : 0);

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getModuleCount() {
        return moduleCount;
    }

    public void setModuleCount(int moduleCount) {
        this.moduleCount = moduleCount;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
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

    public Double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public List<CourseModuleDto> getModules() {
        return modules;
    }

    public void setModules(List<CourseModuleDto> modules) {
        this.modules = modules;
    }
}
