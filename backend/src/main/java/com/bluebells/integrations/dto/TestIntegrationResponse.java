package com.bluebells.integrations.dto;

import java.time.Instant;

public class TestIntegrationResponse {

    private boolean success;
    private String integrationKey;
    private String status;
    private String message;
    private Instant testTimestamp;

    public TestIntegrationResponse() {}

    public TestIntegrationResponse(boolean success, String integrationKey, String status, String message, Instant testTimestamp) {
        this.success = success;
        this.integrationKey = integrationKey;
        this.status = status;
        this.message = message;
        this.testTimestamp = testTimestamp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getIntegrationKey() { return integrationKey; }
    public void setIntegrationKey(String integrationKey) { this.integrationKey = integrationKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getTestTimestamp() { return testTimestamp; }
    public void setTestTimestamp(Instant testTimestamp) { this.testTimestamp = testTimestamp; }
}
