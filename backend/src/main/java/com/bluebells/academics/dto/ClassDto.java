package com.bluebells.academics.dto;

import com.bluebells.academics.ClassEntity;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDto {

    private Long id;
    private Long gradeLevelId;
    private String gradeLevelName;
    private String name;
    private String code;
    private List<SectionDto> sections;
    private List<SubjectDto> subjects;

    public ClassDto() {
    }

    public ClassDto(Long id, Long gradeLevelId, String gradeLevelName, String name, String code, List<SectionDto> sections, List<SubjectDto> subjects) {
        this.id = id;
        this.gradeLevelId = gradeLevelId;
        this.gradeLevelName = gradeLevelName;
        this.name = name;
        this.code = code;
        this.sections = sections;
        this.subjects = subjects;
    }

    public static ClassDto fromEntity(ClassEntity entity) {
        if (entity == null) return null;
        List<SectionDto> sectionDtos = entity.getSections() != null
                ? entity.getSections().stream().map(SectionDto::fromEntity).collect(Collectors.toList())
                : List.of();

        List<SubjectDto> subjectDtos = entity.getSubjects() != null
                ? entity.getSubjects().stream().map(SubjectDto::fromEntity).collect(Collectors.toList())
                : List.of();

        return new ClassDto(
                entity.getId(),
                entity.getGradeLevel() != null ? entity.getGradeLevel().getId() : null,
                entity.getGradeLevel() != null ? entity.getGradeLevel().getName() : null,
                entity.getName(),
                entity.getCode(),
                sectionDtos,
                subjectDtos
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGradeLevelId() {
        return gradeLevelId;
    }

    public void setGradeLevelId(Long gradeLevelId) {
        this.gradeLevelId = gradeLevelId;
    }

    public String getGradeLevelName() {
        return gradeLevelName;
    }

    public void setGradeLevelName(String gradeLevelName) {
        this.gradeLevelName = gradeLevelName;
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

    public List<SectionDto> getSections() {
        return sections;
    }

    public void setSections(List<SectionDto> sections) {
        this.sections = sections;
    }

    public List<SubjectDto> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDto> subjects) {
        this.subjects = subjects;
    }
}
