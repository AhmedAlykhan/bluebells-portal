package com.bluebells.dataimport.dto;

import java.util.ArrayList;
import java.util.List;

public class DataImportPreviewDto {
    private String jobId;
    private String importType;
    private String status;
    private String originalFilename;
    private String fileFormat;
    private Long fileSizeBytes;
    private int totalRows;
    private int validRows;
    private int invalidRows;
    private int duplicateRows;
    private int warningRows;
    private int importedRows;
    private int skippedRows;
    private String importMode;
    private List<DataImportRowPreviewDto> rows = new ArrayList<>();
    private List<DataImportErrorDto> allErrors = new ArrayList<>();
    private int page;
    private int size;
    private int totalFiltered;

    public DataImportPreviewDto() {
    }

    public DataImportPreviewDto(String jobId, String importType, String status, String originalFilename,
                                String fileFormat, Long fileSizeBytes, int totalRows, int validRows,
                                int invalidRows, int duplicateRows, int warningRows, int importedRows,
                                int skippedRows, String importMode, List<DataImportRowPreviewDto> rows,
                                List<DataImportErrorDto> allErrors, int page, int size, int totalFiltered) {
        this.jobId = jobId;
        this.importType = importType;
        this.status = status;
        this.originalFilename = originalFilename;
        this.fileFormat = fileFormat;
        this.fileSizeBytes = fileSizeBytes;
        this.totalRows = totalRows;
        this.validRows = validRows;
        this.invalidRows = invalidRows;
        this.duplicateRows = duplicateRows;
        this.warningRows = warningRows;
        this.importedRows = importedRows;
        this.skippedRows = skippedRows;
        this.importMode = importMode;
        this.rows = rows;
        this.allErrors = allErrors;
        this.page = page;
        this.size = size;
        this.totalFiltered = totalFiltered;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getValidRows() {
        return validRows;
    }

    public void setValidRows(int validRows) {
        this.validRows = validRows;
    }

    public int getInvalidRows() {
        return invalidRows;
    }

    public void setInvalidRows(int invalidRows) {
        this.invalidRows = invalidRows;
    }

    public int getDuplicateRows() {
        return duplicateRows;
    }

    public void setDuplicateRows(int duplicateRows) {
        this.duplicateRows = duplicateRows;
    }

    public int getWarningRows() {
        return warningRows;
    }

    public void setWarningRows(int warningRows) {
        this.warningRows = warningRows;
    }

    public int getImportedRows() {
        return importedRows;
    }

    public void setImportedRows(int importedRows) {
        this.importedRows = importedRows;
    }

    public int getSkippedRows() {
        return skippedRows;
    }

    public void setSkippedRows(int skippedRows) {
        this.skippedRows = skippedRows;
    }

    public String getImportMode() {
        return importMode;
    }

    public void setImportMode(String importMode) {
        this.importMode = importMode;
    }

    public List<DataImportRowPreviewDto> getRows() {
        return rows;
    }

    public void setRows(List<DataImportRowPreviewDto> rows) {
        this.rows = rows;
    }

    public List<DataImportErrorDto> getAllErrors() {
        return allErrors;
    }

    public void setAllErrors(List<DataImportErrorDto> allErrors) {
        this.allErrors = allErrors;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalFiltered() {
        return totalFiltered;
    }

    public void setTotalFiltered(int totalFiltered) {
        this.totalFiltered = totalFiltered;
    }
}
