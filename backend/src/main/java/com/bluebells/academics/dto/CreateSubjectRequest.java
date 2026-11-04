package com.bluebells.academics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSubjectRequest {

    @NotBlank(message = "Subject name is required")
    @Size(max = 128, message = "Subject name must not exceed 128 characters")
    private String name;

    @NotBlank(message = "Subject code is required")
    @Size(max = 32, message = "Subject code must not exceed 32 characters")
    private String code;

    private boolean elective = false;

    @Min(value = 20, message = "Total marks must be at least 20")
    private int totalMarks = 100;

    public CreateSubjectRequest() {
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

    public boolean isElective() {
        return elective;
    }

    public void setElective(boolean elective) {
        this.elective = elective;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }
}
