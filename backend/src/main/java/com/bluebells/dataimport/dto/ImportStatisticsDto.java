package com.bluebells.dataimport.dto;

import java.time.LocalDateTime;

public class ImportStatisticsDto {
    private long totalRecordsImported;
    private long successfulRecords;
    private long failedRecords;
    private long duplicateRecords;
    private long pendingJobs;
    private long totalCompletedJobs;
    private LocalDateTime lastImportDate;
    private String lastImportUser;

    public ImportStatisticsDto() {
    }

    public long getTotalRecordsImported() {
        return totalRecordsImported;
    }

    public void setTotalRecordsImported(long totalRecordsImported) {
        this.totalRecordsImported = totalRecordsImported;
    }

    public long getSuccessfulRecords() {
        return successfulRecords;
    }

    public void setSuccessfulRecords(long successfulRecords) {
        this.successfulRecords = successfulRecords;
    }

    public long getFailedRecords() {
        return failedRecords;
    }

    public void setFailedRecords(long failedRecords) {
        this.failedRecords = failedRecords;
    }

    public long getDuplicateRecords() {
        return duplicateRecords;
    }

    public void setDuplicateRecords(long duplicateRecords) {
        this.duplicateRecords = duplicateRecords;
    }

    public long getPendingJobs() {
        return pendingJobs;
    }

    public void setPendingJobs(long pendingJobs) {
        this.pendingJobs = pendingJobs;
    }

    public long getTotalCompletedJobs() {
        return totalCompletedJobs;
    }

    public void setTotalCompletedJobs(long totalCompletedJobs) {
        this.totalCompletedJobs = totalCompletedJobs;
    }

    public LocalDateTime getLastImportDate() {
        return lastImportDate;
    }

    public void setLastImportDate(LocalDateTime lastImportDate) {
        this.lastImportDate = lastImportDate;
    }

    public String getLastImportUser() {
        return lastImportUser;
    }

    public void setLastImportUser(String lastImportUser) {
        this.lastImportUser = lastImportUser;
    }
}
