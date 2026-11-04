package com.bluebells.academics.dto;

import com.bluebells.academics.GradeLevel;

public class GradeLevelDto {

    private Long id;
    private String name;
    private String stage;
    private int displayOrder;

    public GradeLevelDto() {
    }

    public GradeLevelDto(Long id, String name, String stage, int displayOrder) {
        this.id = id;
        this.name = name;
        this.stage = stage;
        this.displayOrder = displayOrder;
    }

    public static GradeLevelDto fromEntity(GradeLevel entity) {
        if (entity == null) return null;
        return new GradeLevelDto(
                entity.getId(),
                entity.getName(),
                entity.getStage(),
                entity.getDisplayOrder()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
