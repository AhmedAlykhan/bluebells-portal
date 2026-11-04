package com.bluebells.staff.dto;

import com.bluebells.staff.TeacherAssignment;

public class TeacherAssignmentDto {

    private Long id;
    private Long teacherId;
    private String teacherName;
    private Long sectionId;
    private String sectionName;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Long sessionId;
    private String sessionName;
    private boolean classTeacher;

    public TeacherAssignmentDto() {
    }

    public TeacherAssignmentDto(Long id, Long teacherId, String teacherName, Long sectionId, String sectionName, String className, Long subjectId, String subjectName, Long sessionId, String sessionName, boolean classTeacher) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.className = className;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.classTeacher = classTeacher;
    }

    public static TeacherAssignmentDto fromEntity(TeacherAssignment entity) {
        if (entity == null) return null;
        return new TeacherAssignmentDto(
                entity.getId(),
                entity.getTeacher() != null ? entity.getTeacher().getId() : null,
                entity.getTeacher() != null ? entity.getTeacher().getFullName() : null,
                entity.getSection() != null ? entity.getSection().getId() : null,
                entity.getSection() != null ? entity.getSection().getName() : null,
                entity.getSection() != null && entity.getSection().getClassEntity() != null ? entity.getSection().getClassEntity().getName() : null,
                entity.getSubject() != null ? entity.getSubject().getId() : null,
                entity.getSubject() != null ? entity.getSubject().getName() : null,
                entity.getAcademicSession() != null ? entity.getAcademicSession().getId() : null,
                entity.getAcademicSession() != null ? entity.getAcademicSession().getName() : null,
                entity.isClassTeacher()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public boolean isClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(boolean classTeacher) {
        this.classTeacher = classTeacher;
    }
}
