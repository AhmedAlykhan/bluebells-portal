package com.bluebells.academics.dto;

import com.bluebells.academics.Section;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class SectionDto {

    private Long id;
    private Long classId;
    private String name;
    private String roomNumber;
    private int maxCapacity;

    public SectionDto() {
    }

    public SectionDto(Long id, Long classId, String name, String roomNumber, int maxCapacity) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.roomNumber = roomNumber;
        this.maxCapacity = maxCapacity;
    }

    public static SectionDto fromEntity(Section entity) {
        if (entity == null) return null;
        return new SectionDto(
                entity.getId(),
                entity.getClassEntity() != null ? entity.getClassEntity().getId() : null,
                entity.getName(),
                entity.getRoomNumber(),
                entity.getMaxCapacity()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
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
