package com.bluebells.reports.dto;

import java.math.BigDecimal;

public class DashboardDto {

    private long totalStudents;
    private long totalTeachers;
    private BigDecimal totalFeeInvoiced;
    private BigDecimal totalFeeCollected;
    private BigDecimal totalFeeOutstanding;
    private BigDecimal attendanceRateToday;
    private String currentSessionName;

    public DashboardDto() {
    }

    public DashboardDto(long totalStudents, long totalTeachers, BigDecimal totalFeeInvoiced,
                        BigDecimal totalFeeCollected, BigDecimal totalFeeOutstanding,
                        BigDecimal attendanceRateToday, String currentSessionName) {
        this.totalStudents = totalStudents;
        this.totalTeachers = totalTeachers;
        this.totalFeeInvoiced = totalFeeInvoiced;
        this.totalFeeCollected = totalFeeCollected;
        this.totalFeeOutstanding = totalFeeOutstanding;
        this.attendanceRateToday = attendanceRateToday;
        this.currentSessionName = currentSessionName;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public BigDecimal getTotalFeeInvoiced() {
        return totalFeeInvoiced;
    }

    public void setTotalFeeInvoiced(BigDecimal totalFeeInvoiced) {
        this.totalFeeInvoiced = totalFeeInvoiced;
    }

    public BigDecimal getTotalFeeCollected() {
        return totalFeeCollected;
    }

    public void setTotalFeeCollected(BigDecimal totalFeeCollected) {
        this.totalFeeCollected = totalFeeCollected;
    }

    public BigDecimal getTotalFeeOutstanding() {
        return totalFeeOutstanding;
    }

    public void setTotalFeeOutstanding(BigDecimal totalFeeOutstanding) {
        this.totalFeeOutstanding = totalFeeOutstanding;
    }

    public BigDecimal getAttendanceRateToday() {
        return attendanceRateToday;
    }

    public void setAttendanceRateToday(BigDecimal attendanceRateToday) {
        this.attendanceRateToday = attendanceRateToday;
    }

    public String getCurrentSessionName() {
        return currentSessionName;
    }

    public void setCurrentSessionName(String currentSessionName) {
        this.currentSessionName = currentSessionName;
    }
}
