package com.bluebells.development.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateDevelopmentRecordRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Long academicSessionId;

    @NotBlank(message = "Category is required")
    private String category; // ACADEMIC_ACHIEVEMENT, SPORTS, COMPETITION, AWARD, CERTIFICATE, CO_CURRICULAR, PROJECT, POSITIVE_BEHAVIOUR, TEACHER_APPRECIATION, SKILL_MILESTONE

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String eventName;

    @NotNull(message = "Achievement date is required")
    private LocalDate achievementDate;

    private String level; // INTRA_SCHOOL, INTER_SCHOOL, DISTRICT, DIVISION, PROVINCIAL, NATIONAL, INTERNATIONAL

    private String positionOrAward;

    private String teacherAppreciationNote;

    private String skillsAcquired;

    public CreateDevelopmentRecordRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getAcademicSessionId() { return academicSessionId; }
    public void setAcademicSessionId(Long academicSessionId) { this.academicSessionId = academicSessionId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public LocalDate getAchievementDate() { return achievementDate; }
    public void setAchievementDate(LocalDate achievementDate) { this.achievementDate = achievementDate; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getPositionOrAward() { return positionOrAward; }
    public void setPositionOrAward(String positionOrAward) { this.positionOrAward = positionOrAward; }

    public String getTeacherAppreciationNote() { return teacherAppreciationNote; }
    public void setTeacherAppreciationNote(String teacherAppreciationNote) { this.teacherAppreciationNote = teacherAppreciationNote; }

    public String getSkillsAcquired() { return skillsAcquired; }
    public void setSkillsAcquired(String skillsAcquired) { this.skillsAcquired = skillsAcquired; }
}
