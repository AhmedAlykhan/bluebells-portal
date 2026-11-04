package com.bluebells.reports;

import com.bluebells.common.ApiResponse;
import com.bluebells.reports.dto.DashboardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports & Analytics", description = "Executive KPI dashboards and sanitized CSV exports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('REPORTS_VIEW')")
    @Operation(summary = "Get high-level school executive dashboard KPIs")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboardAnalytics() {
        DashboardDto dashboard = reportService.getDashboardAnalytics();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/students/export-csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('REPORTS_VIEW')")
    @Operation(summary = "Export student roster to CSV with formula injection sanitization")
    public ResponseEntity<byte[]> exportStudentsCsv() {
        String csv = reportService.exportStudentsCsv();
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/attendance/export-csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('REPORTS_VIEW')")
    @Operation(summary = "Export daily attendance log to CSV with formula injection sanitization")
    public ResponseEntity<byte[]> exportAttendanceCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String csv = reportService.exportAttendanceCsv(date);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attendance_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/fees/export-csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('REPORTS_VIEW')")
    @Operation(summary = "Export fee ledger to CSV with formula injection sanitization")
    public ResponseEntity<byte[]> exportFeesCsv(
            @RequestParam(required = false) String billingMonth) {
        String csv = reportService.exportFeesCsv(billingMonth);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fees_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
