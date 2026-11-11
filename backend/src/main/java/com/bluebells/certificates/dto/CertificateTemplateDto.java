package com.bluebells.certificates.dto;

public class CertificateTemplateDto {

    private Long id;
    private String title;
    private String certificateType;
    private String headerText;
    private String bodyTemplate;
    private String defaultSignatoryTitle;
    private String defaultSignatoryName;
    private String borderStyle;
    private boolean active;

    public CertificateTemplateDto() {}

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
}
