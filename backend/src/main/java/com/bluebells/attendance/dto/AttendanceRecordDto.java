package com.bluebells.attendance.dto;

import com.bluebells.attendance.StudentAttendance;
import java.time.LocalDate;

public class AttendanceRecordDto {

    private Long id;
    private Long enrollmentId;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private LocalDate attendanceDate;
    private String status;
    private String remarks;
    private String recordedByName;

    public AttendanceRecordDto() {
    }

    public static AttendanceRecordDto fromEntity(StudentAttendance entity) {
        if (entity == null) return null;
        AttendanceRecordDto dto = new AttendanceRecordDto();
        dto.setId(entity.getId());
        if (entity.getEnrollment() != null) {
            dto.setEnrollmentId(entity.getEnrollment().getId());
            dto.setRollNumber(entity.getEnrollment().getRollNumber());
            if (entity.getEnrollment().getStudent() != null) {
                dto.setStudentId(entity.getEnrollment().getStudent().getId());
                dto.setStudentName(entity.getEnrollment().getStudent().getFullName());
            }
        }
        dto.setAttendanceDate(entity.getAttendanceDate());
        dto.setStatus(entity.getStatus());
        dto.setRemarks(entity.getRemarks());
        if (entity.getRecordedBy() != null) {
            dto.setRecordedByName(entity.getRecordedBy().getFullName());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
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

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRecordedByName() {
        return recordedByName;
    }

    public void setRecordedByName(String recordedByName) {
        this.recordedByName = recordedByName;
    }
}
