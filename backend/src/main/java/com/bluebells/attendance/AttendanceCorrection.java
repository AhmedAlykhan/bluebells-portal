package com.bluebells.attendance;

import com.bluebells.identity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "attendance_corrections")
public class AttendanceCorrection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private StudentAttendance attendance;

    @Column(name = "old_status", nullable = false, length = 16)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 16)
    private String newStatus;

    @Column(nullable = false, length = 255)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(nullable = false, length = 16)
    private String status = "APPROVED";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public AttendanceCorrection() {
    }

    public AttendanceCorrection(StudentAttendance attendance, String oldStatus, String newStatus, String reason, User requestedBy, User approvedBy) {
        this.attendance = attendance;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.status = "APPROVED";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentAttendance getAttendance() {
        return attendance;
    }

    public void setAttendance(StudentAttendance attendance) {
        this.attendance = attendance;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
