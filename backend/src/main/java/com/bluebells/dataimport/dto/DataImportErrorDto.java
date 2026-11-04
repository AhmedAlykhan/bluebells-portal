package com.bluebells.dataimport.dto;

public class DataImportErrorDto {
    private Long id;
    private int rowNumber;
    private String sheetName;
    private String fieldName;
    private String fieldValue;
    private String errorCode;
    private String errorMessage;
    private String severity;

    public DataImportErrorDto() {
    }

    public DataImportErrorDto(int rowNumber, String sheetName, String fieldName, String fieldValue, String errorCode, String errorMessage, String severity) {
        this.rowNumber = rowNumber;
        this.sheetName = sheetName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.severity = severity;
    }

    public DataImportErrorDto(Long id, int rowNumber, String sheetName, String fieldName, String fieldValue, String errorCode, String errorMessage, String severity) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.sheetName = sheetName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.severity = severity;
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
