package com.bluebells.integrations;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "school_integrations")
public class SchoolIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "integration_key", nullable = false, unique = true, length = 50)
    private String integrationKey; // PUSH_NOTIFICATIONS, SMS_GATEWAY, WHATSAPP_BUSINESS, ONLINE_FEE_PAYMENT, GPS_TRANSPORT, QR_ATTENDANCE, EXTERNAL_CALENDAR

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category; // COMMUNICATION, FINANCE, TRANSPORT, ATTENDANCE, CALENDAR

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 50)
    private String status = "READY_FOR_CREDENTIALS"; // CONFIGURED_OPERATIONAL, READY_FOR_CREDENTIALS, DISABLED, MAINTENANCE

    @Column(name = "config_schema", columnDefinition = "TEXT")
    private String configSchema;

    @Column(name = "config_values", columnDefinition = "TEXT")
    private String configValues;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = false;

    @Column(name = "last_heartbeat")
    private Instant lastHeartbeat;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public SchoolIntegration() {}

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

    public String getConfigSchema() { return configSchema; }
    public void setConfigSchema(String configSchema) { this.configSchema = configSchema; }

    public String getConfigValues() { return configValues; }
    public void setConfigValues(String configValues) { this.configValues = configValues; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
