package com.bluebells.academics.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSectionRequest {

    @NotBlank(message = "Section name is required (e.g. A, B, Rose, Jasmine)")
    @Size(max = 32, message = "Section name must not exceed 32 characters")
    private String name;

    private String roomNumber;

    @Min(value = 5, message = "Max capacity must be at least 5 students")
    @Max(value = 60, message = "Max capacity cannot exceed 60 students")
    private int maxCapacity = 35;

    public CreateSectionRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
