package com.bluebells.development;

import com.bluebells.academics.AcademicSession;
import com.bluebells.identity.User;
import com.bluebells.students.Student;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "student_development_records")
public class StudentDevelopmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_name", length = 255)
    private String eventName;

    @Column(name = "achievement_date", nullable = false)
    private LocalDate achievementDate;

    @Column(name = "`level`", length = 50)
    private String level;

    @Column(name = "position_or_award", length = 100)
    private String positionOrAward;

    @Column(name = "evidence_file_path", length = 500)
    private String evidenceFilePath;

    @Column(name = "evidence_file_name", length = 255)
    private String evidenceFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @Column(name = "teacher_appreciation_note", columnDefinition = "TEXT")
    private String teacherAppreciationNote;

    @Column(name = "skills_acquired", length = 500)
    private String skillsAcquired;

    @Column(nullable = false, length = 50)
    private String status = "VERIFIED";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public StudentDevelopmentRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public AcademicSession getAcademicSession() { return academicSession; }
    public void setAcademicSession(AcademicSession academicSession) { this.academicSession = academicSession; }

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

    public User getRecordedBy() { return recordedBy; }
    public void setRecordedBy(User recordedBy) { this.recordedBy = recordedBy; }

    public String getTeacherAppreciationNote() { return teacherAppreciationNote; }
    public void setTeacherAppreciationNote(String teacherAppreciationNote) { this.teacherAppreciationNote = teacherAppreciationNote; }

    public String getSkillsAcquired() { return skillsAcquired; }
    public void setSkillsAcquired(String skillsAcquired) { this.skillsAcquired = skillsAcquired; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
