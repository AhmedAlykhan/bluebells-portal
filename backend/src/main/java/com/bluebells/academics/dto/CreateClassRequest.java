package com.bluebells.academics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateClassRequest {

    @NotNull(message = "Grade level is required")
    private Long gradeLevelId;

    @NotBlank(message = "Class name is required (e.g. Class 9, Class 10)")
    @Size(max = 64, message = "Class name must not exceed 64 characters")
    private String name;

    @NotBlank(message = "Unique class code is required (e.g. CLS-9)")
    @Size(max = 32, message = "Class code must not exceed 32 characters")
    private String code;

    public CreateClassRequest() {
    }

    public Long getGradeLevelId() {
        return gradeLevelId;
    }

    public void setGradeLevelId(Long gradeLevelId) {
        this.gradeLevelId = gradeLevelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
