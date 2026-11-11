package com.bluebells.integrations.dto;

public class UpdateIntegrationRequest {

    private boolean enabled;
    private String status;
    private String configValues;

    public UpdateIntegrationRequest() {}

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConfigValues() { return configValues; }
    public void setConfigValues(String configValues) { this.configValues = configValues; }
}
