package com.bluebells.academics.dto;

import com.bluebells.academics.Subject;

public class SubjectDto {

    private Long id;
    private Long classId;
    private String name;
    private String code;
    private boolean elective;
    private int totalMarks;

    public SubjectDto() {
    }

    public SubjectDto(Long id, Long classId, String name, String code, boolean elective, int totalMarks) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.code = code;
        this.elective = elective;
        this.totalMarks = totalMarks;
    }

    public static SubjectDto fromEntity(Subject entity) {
        if (entity == null) return null;
        return new SubjectDto(
                entity.getId(),
                entity.getClassEntity() != null ? entity.getClassEntity().getId() : null,
                entity.getName(),
                entity.getCode(),
                entity.isElective(),
                entity.getTotalMarks()
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
