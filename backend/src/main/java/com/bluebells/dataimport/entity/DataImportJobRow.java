package com.bluebells.dataimport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_import_job_rows")
public class DataImportJobRow {

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

    @Column(name = "entity_identifier", length = 64)
    private String entityIdentifier;

    @Column(name = "row_data_json", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String rowDataJson;

    @Column(nullable = false, length = 32)
    private String status = "VALID"; // VALID, INVALID, DUPLICATE, WARNING, IMPORTED, SKIPPED, FAILED

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DataImportJobRow() {
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

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public String getRowDataJson() {
        return rowDataJson;
    }

    public void setRowDataJson(String rowDataJson) {
        this.rowDataJson = rowDataJson;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
