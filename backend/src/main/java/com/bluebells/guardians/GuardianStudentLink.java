package com.bluebells.guardians;

import com.bluebells.students.Student;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "guardian_student_links")
public class GuardianStudentLink {

    @EmbeddedId
    private GuardianStudentLinkId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("guardianId")
    @JoinColumn(name = "guardian_id")
    private Guardian guardian;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "is_primary_contact", nullable = false)
    private boolean primaryContact = true;

    @Column(name = "is_financial_sponsor", nullable = false)
    private boolean financialSponsor = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public GuardianStudentLink() {
    }

    public GuardianStudentLink(Guardian guardian, Student student, boolean primaryContact, boolean financialSponsor) {
        this.id = new GuardianStudentLinkId(guardian.getId(), student.getId());
        this.guardian = guardian;
        this.student = student;
        this.primaryContact = primaryContact;
        this.financialSponsor = financialSponsor;
    }

    public GuardianStudentLinkId getId() {
        return id;
    }

    public void setId(GuardianStudentLinkId id) {
        this.id = id;
    }

    public Guardian getGuardian() {
        return guardian;
    }

    public void setGuardian(Guardian guardian) {
        this.guardian = guardian;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(boolean primaryContact) {
        this.primaryContact = primaryContact;
    }

    public boolean isFinancialSponsor() {
        return financialSponsor;
    }

    public void setFinancialSponsor(boolean financialSponsor) {
        this.financialSponsor = financialSponsor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
