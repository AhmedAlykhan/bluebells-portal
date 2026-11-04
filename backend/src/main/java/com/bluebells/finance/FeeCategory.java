package com.bluebells.finance;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "fee_categories")
public class FeeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @Column(name = "is_recurring", nullable = false)
    private boolean recurring = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public FeeCategory() {
    }

    public FeeCategory(String name, boolean recurring) {
        this.name = name;
        this.recurring = recurring;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
