package com.bluebells.dataimport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_import_errors")
public class DataImportError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private DataImportJob job;

    @Column(name = "`row_number`", nullable = false)
    private int rowNumber;

    @Column(name = "sheet_name", nullable = false, length = 64)
    private String sheetName = "Main";

    @Column(name = "field_name", nullable = false, length = 64)
    private String fieldName;

    @Column(name = "field_value", length = 255)
    private String fieldValue;

    @Column(name = "error_code", nullable = false, length = 64)
    private String errorCode;

    @Column(name = "error_message", nullable = false, length = 512)
    private String errorMessage;

    @Column(nullable = false, length = 16)
    private String severity = "ERROR"; // ERROR, WARNING

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DataImportError() {
    }

    public DataImportError(DataImportJob job, int rowNumber, String sheetName, String fieldName, String fieldValue, String errorCode, String errorMessage, String severity) {
        this.job = job;
        this.rowNumber = rowNumber;
        this.sheetName = sheetName != null ? sheetName : "Main";
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.severity = severity != null ? severity : "ERROR";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataImportJob getJob() {
        return job;
    }

    public void setJob(DataImportJob job) {
        this.job = job;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
