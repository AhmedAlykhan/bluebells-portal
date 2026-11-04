package com.bluebells.dataimport.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataImportRowPreviewDto {
    private Long id;
    private int rowNumber;
    private String sheetName;
    private String entityIdentifier;
    private String status; // VALID, INVALID, DUPLICATE, WARNING, IMPORTED, SKIPPED, FAILED
    private String errorMessage;
    private Map<String, Object> rowData = new HashMap<>();
    private List<DataImportErrorDto> errors = new ArrayList<>();

    public DataImportRowPreviewDto() {
    }

    @SuppressWarnings("unchecked")
    public DataImportRowPreviewDto(Long id, int rowNumber, String sheetName, String entityIdentifier, Map<String, ?> rowData, String status, String errorMessage) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.sheetName = sheetName;
        this.entityIdentifier = entityIdentifier;
        if (rowData != null) {
            this.rowData = new HashMap<>(rowData);
        }
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getRowData() {
        return rowData;
    }

    public void setRowData(Map<String, Object> rowData) {
        this.rowData = rowData;
    }

    public List<DataImportErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<DataImportErrorDto> errors) {
        this.errors = errors;
    }
}
