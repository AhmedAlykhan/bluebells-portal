package com.bluebells.reports;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.attendance.StudentAttendance;
import com.bluebells.attendance.StudentAttendanceRepository;
import com.bluebells.finance.FeeInvoice;
import com.bluebells.finance.FeeInvoiceRepository;
import com.bluebells.reports.dto.DashboardDto;
import com.bluebells.staff.StaffRepository;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final AcademicSessionRepository sessionRepository;
    private final FeeInvoiceRepository invoiceRepository;
    private final StudentAttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ReportService(
            StudentRepository studentRepository,
            StaffRepository staffRepository,
            AcademicSessionRepository sessionRepository,
            FeeInvoiceRepository invoiceRepository,
            StudentAttendanceRepository attendanceRepository,
            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.sessionRepository = sessionRepository;
        this.invoiceRepository = invoiceRepository;
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboardAnalytics() {
        long totalStudents = studentRepository.count();
        long totalTeachers = staffRepository.count();

        AcademicSession currentSession = sessionRepository.findByCurrentTrue().orElse(null);
        String sessionName = currentSession != null ? currentSession.getName() : "None";

        List<FeeInvoice> allInvoices = invoiceRepository.findAll();
        BigDecimal totalInvoiced = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalOutstanding = BigDecimal.ZERO;

        for (FeeInvoice inv : allInvoices) {
            totalInvoiced = totalInvoiced.add(inv.getTotalAmount());
            totalCollected = totalCollected.add(inv.getPaidAmount());
            totalOutstanding = totalOutstanding.add(inv.getBalanceDue());
        }

        List<StudentAttendance> todayAttendance = attendanceRepository.findByAttendanceDate(LocalDate.now());
        BigDecimal attendanceRate = new BigDecimal("100.00");
        if (!todayAttendance.isEmpty()) {
            long presentCount = todayAttendance.stream()
                    .filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus()) || "LATE".equalsIgnoreCase(a.getStatus()))
                    .count();
            attendanceRate = BigDecimal.valueOf(presentCount)
                    .divide(BigDecimal.valueOf(todayAttendance.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return new DashboardDto(
                totalStudents,
                totalTeachers,
                totalInvoiced.setScale(2, RoundingMode.HALF_UP),
                totalCollected.setScale(2, RoundingMode.HALF_UP),
                totalOutstanding.setScale(2, RoundingMode.HALF_UP),
                attendanceRate,
                sessionName
        );
    }

    @Transactional(readOnly = true)
    public String exportStudentsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Admission Number,Full Name,Gender,Date of Birth,Status,Residential Address\n");

        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            sb.append(CsvSanitizer.sanitize(s.getId())).append(",")
              .append(CsvSanitizer.sanitize(s.getAdmissionNumber())).append(",")
              .append(CsvSanitizer.sanitize(s.getFullName())).append(",")
              .append(CsvSanitizer.sanitize(s.getGender())).append(",")
              .append(CsvSanitizer.sanitize(s.getDateOfBirth())).append(",")
              .append(CsvSanitizer.sanitize(s.getStatus())).append(",")
              .append(CsvSanitizer.sanitize(s.getResidentialAddress()))
              .append("\n");
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportAttendanceCsv(LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        StringBuilder sb = new StringBuilder();
        sb.append("Record ID,Date,Admission Number,Student Name,Class,Section,Status,Remarks\n");

        List<StudentAttendance> records = attendanceRepository.findByAttendanceDate(date);
        for (StudentAttendance a : records) {
            Enrollment e = a.getEnrollment();
            String className = e.getSection().getClassEntity().getName();
            String sectionName = e.getSection().getName();

            sb.append(CsvSanitizer.sanitize(a.getId())).append(",")
              .append(CsvSanitizer.sanitize(a.getAttendanceDate())).append(",")
              .append(CsvSanitizer.sanitize(e.getStudent().getAdmissionNumber())).append(",")
              .append(CsvSanitizer.sanitize(e.getStudent().getFullName())).append(",")
              .append(CsvSanitizer.sanitize(className)).append(",")
              .append(CsvSanitizer.sanitize(sectionName)).append(",")
              .append(CsvSanitizer.sanitize(a.getStatus())).append(",")
              .append(CsvSanitizer.sanitize(a.getRemarks()))
              .append("\n");
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportFeesCsv(String billingMonth) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice Number,Billing Month,Admission Number,Student Name,Total Amount,Discount,Paid Amount,Balance Due,Status,Due Date\n");

        List<FeeInvoice> invoices;
        if (billingMonth != null && !billingMonth.isBlank()) {
            invoices = invoiceRepository.findAll().stream()
                    .filter(inv -> billingMonth.equalsIgnoreCase(inv.getBillingMonth()))
                    .collect(Collectors.toList());
        } else {
            invoices = invoiceRepository.findAll();
        }

        for (FeeInvoice inv : invoices) {
            sb.append(CsvSanitizer.sanitize(inv.getInvoiceNumber())).append(",")
              .append(CsvSanitizer.sanitize(inv.getBillingMonth())).append(",")
              .append(CsvSanitizer.sanitize(inv.getStudent().getAdmissionNumber())).append(",")
              .append(CsvSanitizer.sanitize(inv.getStudent().getFullName())).append(",")
              .append(CsvSanitizer.sanitize(inv.getTotalAmount())).append(",")
              .append(CsvSanitizer.sanitize(inv.getDiscountAmount())).append(",")
              .append(CsvSanitizer.sanitize(inv.getPaidAmount())).append(",")
              .append(CsvSanitizer.sanitize(inv.getBalanceDue())).append(",")
              .append(CsvSanitizer.sanitize(inv.getStatus())).append(",")
              .append(CsvSanitizer.sanitize(inv.getDueDate()))
              .append("\n");
        }
        return sb.toString();
    }
}
