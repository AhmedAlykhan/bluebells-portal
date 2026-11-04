package com.bluebells.dataimport.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ConfirmImportRequest {

    @NotNull(message = "Import mode is required")
    @Pattern(regexp = "STRICT|VALID_ONLY", message = "Import mode must be either STRICT or VALID_ONLY")
    private String importMode = "STRICT";

    private String notes;

    public ConfirmImportRequest() {
    }

    public ConfirmImportRequest(String importMode, String notes) {
        this.importMode = importMode;
        this.notes = notes;
    }

    public String getImportMode() {
        return importMode;
    }

    public void setImportMode(String importMode) {
        this.importMode = importMode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
