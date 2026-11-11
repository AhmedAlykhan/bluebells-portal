package com.bluebells.idcard;

import com.bluebells.academics.AcademicSession;
import com.bluebells.identity.User;
import com.bluebells.students.Student;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "student_id_cards")
public class StudentIdCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @Column(name = "card_number", nullable = false, unique = true, length = 50)
    private String cardNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, length = 50)
    private String status = "ACTIVE"; // ACTIVE, REISSUED, LOST, DAMAGED, CANCELLED

    @Column(name = "reissue_count", nullable = false)
    private int reissueCount = 0;

    @Column(name = "reissue_reason", length = 255)
    private String reissueReason;

    @Column(name = "reissued_at")
    private Instant reissuedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reissued_by")
    private User reissuedBy;

    @Column(name = "qr_verification_token", nullable = false, unique = true, length = 255)
    private String qrVerificationToken;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public StudentIdCard() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public AcademicSession getAcademicSession() { return academicSession; }
    public void setAcademicSession(AcademicSession academicSession) { this.academicSession = academicSession; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getReissueCount() { return reissueCount; }
    public void setReissueCount(int reissueCount) { this.reissueCount = reissueCount; }

    public String getReissueReason() { return reissueReason; }
    public void setReissueReason(String reissueReason) { this.reissueReason = reissueReason; }

    public Instant getReissuedAt() { return reissuedAt; }
    public void setReissuedAt(Instant reissuedAt) { this.reissuedAt = reissuedAt; }

    public User getReissuedBy() { return reissuedBy; }
    public void setReissuedBy(User reissuedBy) { this.reissuedBy = reissuedBy; }

    public String getQrVerificationToken() { return qrVerificationToken; }
    public void setQrVerificationToken(String qrVerificationToken) { this.qrVerificationToken = qrVerificationToken; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
