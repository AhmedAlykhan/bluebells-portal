package com.bluebells.integrations.dto;

import java.time.Instant;

public class SchoolIntegrationDto {

    private Long id;
    private String integrationKey;
    private String name;
    private String category;
    private String description;
    private String status; // CONFIGURED_OPERATIONAL, READY_FOR_CREDENTIALS, DISABLED, MAINTENANCE
    private boolean enabled;
    private Instant lastHeartbeat;
    private Instant updatedAt;

    public SchoolIntegrationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIntegrationKey() { return integrationKey; }
    public void setIntegrationKey(String integrationKey) { this.integrationKey = integrationKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
