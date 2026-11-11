package com.bluebells.certificates;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "certificate_templates")
public class CertificateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "certificate_type", nullable = false, length = 50)
    private String certificateType; // MERIT, ACHIEVEMENT, PARTICIPATION, SPORTS, COMPLETION, CONDUCT

    @Column(name = "header_text", nullable = false, length = 255)
    private String headerText;

    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Column(name = "default_signatory_title", nullable = false, length = 100)
    private String defaultSignatoryTitle = "Principal";

    @Column(name = "default_signatory_name", nullable = false, length = 100)
    private String defaultSignatoryName = "Huma Arif";

    @Column(name = "border_style", nullable = false, length = 50)
    private String borderStyle = "ROYAL_GOLD";

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public CertificateTemplate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }

    public String getHeaderText() { return headerText; }
    public void setHeaderText(String headerText) { this.headerText = headerText; }

    public String getBodyTemplate() { return bodyTemplate; }
    public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }

    public String getDefaultSignatoryTitle() { return defaultSignatoryTitle; }
    public void setDefaultSignatoryTitle(String defaultSignatoryTitle) { this.defaultSignatoryTitle = defaultSignatoryTitle; }

    public String getDefaultSignatoryName() { return defaultSignatoryName; }
    public void setDefaultSignatoryName(String defaultSignatoryName) { this.defaultSignatoryName = defaultSignatoryName; }

    public String getBorderStyle() { return borderStyle; }
    public void setBorderStyle(String borderStyle) { this.borderStyle = borderStyle; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
