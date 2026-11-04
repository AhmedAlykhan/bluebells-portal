package com.bluebells.guardians;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GuardianStudentLinkId implements Serializable {

    @Column(name = "guardian_id")
    private Long guardianId;

    @Column(name = "student_id")
    private Long studentId;

    public GuardianStudentLinkId() {
    }

    public GuardianStudentLinkId(Long guardianId, Long studentId) {
        this.guardianId = guardianId;
        this.studentId = studentId;
    }

    public Long getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(Long guardianId) {
        this.guardianId = guardianId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuardianStudentLinkId that = (GuardianStudentLinkId) o;
        return Objects.equals(guardianId, that.guardianId) && Objects.equals(studentId, that.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guardianId, studentId);
    }
}
