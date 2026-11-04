package com.bluebells.guardians.dto;

import jakarta.validation.constraints.NotNull;

public class LinkGuardianRequest {

    @NotNull(message = "Guardian ID is required")
    private Long guardianId;

    private boolean primaryContact = true;
    private boolean financialSponsor = true;

    public LinkGuardianRequest() {
    }

    public LinkGuardianRequest(Long guardianId, boolean primaryContact, boolean financialSponsor) {
        this.guardianId = guardianId;
        this.primaryContact = primaryContact;
        this.financialSponsor = financialSponsor;
    }

    public Long getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(Long guardianId) {
        this.guardianId = guardianId;
    }

    public boolean isPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(boolean primaryContact) {
        this.primaryContact = primaryContact;
    }

    public boolean isFinancialSponsor() {
        return financialSponsor;
    }

    public void setFinancialSponsor(boolean financialSponsor) {
        this.financialSponsor = financialSponsor;
    }
}
