package com.bluebells.examination.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RecordMarksRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotEmpty(message = "Marks items cannot be empty")
    @Valid
    private List<EnterMarksItemRequest> items;

    public RecordMarksRequest() {
    }

    public RecordMarksRequest(Long examId, Long subjectId, List<EnterMarksItemRequest> items) {
        this.examId = examId;
        this.subjectId = subjectId;
        this.items = items;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public List<EnterMarksItemRequest> getItems() {
        return items;
    }

    public void setItems(List<EnterMarksItemRequest> items) {
        this.items = items;
    }
}
