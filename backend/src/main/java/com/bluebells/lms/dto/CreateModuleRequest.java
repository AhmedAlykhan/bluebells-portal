package com.bluebells.lms.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateModuleRequest {

    @NotBlank(message = "Module title is required")
    private String title;

    private String description;
    private int displayOrder = 1;

    public CreateModuleRequest() {
    }

    public CreateModuleRequest(String title, String description, int displayOrder) {
        this.title = title;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
