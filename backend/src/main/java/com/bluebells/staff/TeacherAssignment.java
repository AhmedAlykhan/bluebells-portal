package com.bluebells.staff;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.Section;
import com.bluebells.academics.Subject;
import com.bluebells.identity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "teacher_assignments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"teacher_id", "section_id", "subject_id", "session_id"})
})
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession academicSession;

    @Column(name = "is_class_teacher", nullable = false)
    private boolean classTeacher = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public TeacherAssignment() {
    }

    public TeacherAssignment(User teacher, Section section, Subject subject, AcademicSession academicSession, boolean classTeacher) {
        this.teacher = teacher;
        this.section = section;
        this.subject = subject;
        this.academicSession = academicSession;
        this.classTeacher = classTeacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public AcademicSession getAcademicSession() {
        return academicSession;
    }

    public void setAcademicSession(AcademicSession academicSession) {
        this.academicSession = academicSession;
    }

    public boolean isClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(boolean classTeacher) {
        this.classTeacher = classTeacher;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
