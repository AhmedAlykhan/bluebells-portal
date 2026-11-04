package com.bluebells.examination.dto;

import com.bluebells.examination.ReportCard;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ReportCardDto {

    private Long id;
    private Long examId;
    private String examName;
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String className;
    private String sectionName;
    private String rollNumber;
    private BigDecimal totalMarksObtained;
    private BigDecimal totalMaxMarks;
    private BigDecimal overallPercentage;
    private String overallGrade;
    private BigDecimal attendancePercentage;
    private Integer classRank;
    private String remarks;
    private Instant generatedAt;
    private List<SubjectMarkDto> subjectMarks = new ArrayList<>();

    public ReportCardDto() {
    }

    public static ReportCardDto fromEntity(ReportCard entity) {
        if (entity == null) return null;
        ReportCardDto dto = new ReportCardDto();
        dto.setId(entity.getId());
        if (entity.getExam() != null) {
            dto.setExamId(entity.getExam().getId());
            dto.setExamName(entity.getExam().getName());
        }
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getFullName());
            dto.setAdmissionNumber(entity.getStudent().getAdmissionNumber());
        }
        if (entity.getEnrollment() != null) {
            dto.setRollNumber(entity.getEnrollment().getRollNumber());
            if (entity.getEnrollment().getSection() != null) {
                dto.setSectionName(entity.getEnrollment().getSection().getName());
                if (entity.getEnrollment().getSection().getClassEntity() != null) {
                    dto.setClassName(entity.getEnrollment().getSection().getClassEntity().getName());
                }
            }
        }
        dto.setTotalMarksObtained(entity.getTotalMarksObtained());
        dto.setTotalMaxMarks(entity.getTotalMaxMarks());
        dto.setOverallPercentage(entity.getOverallPercentage());
        dto.setOverallGrade(entity.getOverallGrade());
        dto.setAttendancePercentage(entity.getAttendancePercentage());
        dto.setClassRank(entity.getClassRank());
        dto.setRemarks(entity.getRemarks());
        dto.setGeneratedAt(entity.getGeneratedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
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

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public BigDecimal getTotalMarksObtained() {
        return totalMarksObtained;
    }

    public void setTotalMarksObtained(BigDecimal totalMarksObtained) {
        this.totalMarksObtained = totalMarksObtained;
    }

    public BigDecimal getTotalMaxMarks() {
        return totalMaxMarks;
    }

    public void setTotalMaxMarks(BigDecimal totalMaxMarks) {
        this.totalMaxMarks = totalMaxMarks;
    }

    public BigDecimal getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(BigDecimal overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public String getOverallGrade() {
        return overallGrade;
    }

    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }

    public BigDecimal getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(BigDecimal attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public Integer getClassRank() {
        return classRank;
    }

    public void setClassRank(Integer classRank) {
        this.classRank = classRank;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<SubjectMarkDto> getSubjectMarks() {
        return subjectMarks;
    }

    public void setSubjectMarks(List<SubjectMarkDto> subjectMarks) {
        this.subjectMarks = subjectMarks;
    }
}
