package com.bluebells.development.dto;

import java.time.Instant;
import java.time.LocalDate;

public class StudentDevelopmentDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private Long academicSessionId;
    private String sessionName;
    private String category;
    private String title;
    private String description;
    private String eventName;
    private LocalDate achievementDate;
    private String level;
    private String positionOrAward;
    private String evidenceFilePath;
    private String evidenceFileName;
    private Long recordedById;
    private String recordedByName;
    private String teacherAppreciationNote;
    private String skillsAcquired;
    private String status;
    private Instant createdAt;

    public StudentDevelopmentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public Long getAcademicSessionId() { return academicSessionId; }
    public void setAcademicSessionId(Long academicSessionId) { this.academicSessionId = academicSessionId; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

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

    public String getEvidenceFilePath() { return evidenceFilePath; }
    public void setEvidenceFilePath(String evidenceFilePath) { this.evidenceFilePath = evidenceFilePath; }

    public String getEvidenceFileName() { return evidenceFileName; }
    public void setEvidenceFileName(String evidenceFileName) { this.evidenceFileName = evidenceFileName; }

    public Long getRecordedById() { return recordedById; }
    public void setRecordedById(Long recordedById) { this.recordedById = recordedById; }

    public String getRecordedByName() { return recordedByName; }
    public void setRecordedByName(String recordedByName) { this.recordedByName = recordedByName; }

    public String getTeacherAppreciationNote() { return teacherAppreciationNote; }
    public void setTeacherAppreciationNote(String teacherAppreciationNote) { this.teacherAppreciationNote = teacherAppreciationNote; }

    public String getSkillsAcquired() { return skillsAcquired; }
    public void setSkillsAcquired(String skillsAcquired) { this.skillsAcquired = skillsAcquired; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
