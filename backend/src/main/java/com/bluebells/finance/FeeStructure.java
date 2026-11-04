package com.bluebells.finance;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.ClassEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "fee_structures", uniqueConstraints = {
        @UniqueConstraint(name = "uk_fee_structure", columnNames = {"session_id", "class_id", "category_id"})
})
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private AcademicSession academicSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private FeeCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 32)
    private String frequency = "MONTHLY";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public FeeStructure() {
    }

    public FeeStructure(AcademicSession academicSession, ClassEntity classEntity, FeeCategory category, BigDecimal amount, String frequency) {
        this.academicSession = academicSession;
        this.classEntity = classEntity;
        this.category = category;
        this.amount = amount;
        this.frequency = frequency != null ? frequency : "MONTHLY";
    }

    public Long getId() {
        return id;
    }

    public AcademicSession getAcademicSession() {
        return academicSession;
    }

    public void setAcademicSession(AcademicSession academicSession) {
        this.academicSession = academicSession;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public FeeCategory getCategory() {
        return category;
    }

    public void setCategory(FeeCategory category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
