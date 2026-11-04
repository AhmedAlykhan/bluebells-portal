package com.bluebells.attendance.dto;

public class AttendanceSummaryDto {

    private Long studentId;
    private String studentName;
    private int totalDays;
    private int presentDays;
    private int absentDays;
    private int lateDays;
    private int excusedDays;
    private double attendancePercentage;

    public AttendanceSummaryDto() {
    }

    public AttendanceSummaryDto(Long studentId, String studentName, int totalDays, int presentDays,
                                int absentDays, int lateDays, int excusedDays, double attendancePercentage) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalDays = totalDays;
        this.presentDays = presentDays;
        this.absentDays = absentDays;
        this.lateDays = lateDays;
        this.excusedDays = excusedDays;
        this.attendancePercentage = attendancePercentage;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public int getLateDays() {
        return lateDays;
    }

    public void setLateDays(int lateDays) {
        this.lateDays = lateDays;
    }

    public int getExcusedDays() {
        return excusedDays;
    }

    public void setExcusedDays(int excusedDays) {
        this.excusedDays = excusedDays;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
