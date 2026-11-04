package com.bluebells.examination;

import com.bluebells.students.Enrollment;
import com.bluebells.students.Student;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "report_cards", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_id", "student_id"})
})
public class ReportCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "total_marks_obtained", nullable = false, precision = 7, scale = 2)
    private BigDecimal totalMarksObtained;

    @Column(name = "total_max_marks", nullable = false, precision = 7, scale = 2)
    private BigDecimal totalMaxMarks;

    @Column(name = "overall_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallPercentage;

    @Column(name = "overall_grade", nullable = false, length = 8)
    private String overallGrade;

    @Column(name = "attendance_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal attendancePercentage;

    @Column(name = "class_rank")
    private Integer classRank;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(nullable = false, length = 32)
    private String status = "PUBLISHED";

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt = Instant.now();

    public ReportCard() {
    }

    public ReportCard(Exam exam, Student student, Enrollment enrollment, BigDecimal totalMarksObtained,
                      BigDecimal totalMaxMarks, BigDecimal overallPercentage, String overallGrade,
                      BigDecimal attendancePercentage, Integer classRank, String remarks) {
        this.exam = exam;
        this.student = student;
        this.enrollment = enrollment;
        this.totalMarksObtained = totalMarksObtained;
        this.totalMaxMarks = totalMaxMarks;
        this.overallPercentage = overallPercentage;
        this.overallGrade = overallGrade;
        this.attendancePercentage = attendancePercentage;
        this.classRank = classRank;
        this.remarks = remarks;
        this.status = "PUBLISHED";
        this.generatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
