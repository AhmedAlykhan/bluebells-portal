package com.bluebells.analytics;

import com.bluebells.analytics.dto.AnalyticsFilterDto;
import com.bluebells.analytics.dto.ManagementAnalyticsDto;
import com.bluebells.analytics.dto.ManagementAnalyticsDto.*;
import com.bluebells.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Institutional Analytics", description = "Real-time School Management Intelligence & Analytics APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get comprehensive school management analytics across all domains")
    public ResponseEntity<ApiResponse<ManagementAnalyticsDto>> getFullAnalytics(AnalyticsFilterDto filter) {
        ManagementAnalyticsDto dto = analyticsService.getFullManagementAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get student demographics, strength, and session comparison analytics")
    public ResponseEntity<ApiResponse<StudentAnalyticsDto>> getStudentAnalytics(AnalyticsFilterDto filter) {
        StudentAnalyticsDto dto = analyticsService.getStudentAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get attendance rates, absenteeism trends, and chronic absentees")
    public ResponseEntity<ApiResponse<AttendanceAnalyticsDto>> getAttendanceAnalytics(AnalyticsFilterDto filter) {
        AttendanceAnalyticsDto dto = analyticsService.getAttendanceAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/academics")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get examination performance, class averages, and academic support alerts")
    public ResponseEntity<ApiResponse<AcademicAnalyticsDto>> getAcademicAnalytics(AnalyticsFilterDto filter) {
        AcademicAnalyticsDto dto = analyticsService.getAcademicAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/financial")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get fee billing, collection rates, monthly revenue, and class-wise balances")
    public ResponseEntity<ApiResponse<FinancialAnalyticsDto>> getFinancialAnalytics(AnalyticsFilterDto filter) {
        FinancialAnalyticsDto dto = analyticsService.getFinancialAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/lms")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get LMS engagement, assignment completion rates, and learning material stats")
    public ResponseEntity<ApiResponse<LmsAnalyticsDto>> getLmsAnalytics(AnalyticsFilterDto filter) {
        LmsAnalyticsDto dto = analyticsService.getLmsAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ANALYTICS_VIEW')")
    @Operation(summary = "Get staff department breakdown, teacher workloads, and assignment statistics")
    public ResponseEntity<ApiResponse<StaffAnalyticsDto>> getStaffAnalytics(AnalyticsFilterDto filter) {
        StaffAnalyticsDto dto = analyticsService.getStaffAnalytics(filter);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('ANALYTICS_EXPORT')")
    @Operation(summary = "Export multi-sheet executive analytics workbook (.xlsx)")
    public ResponseEntity<byte[]> exportAnalyticsExcel(AnalyticsFilterDto filter) throws IOException {
        byte[] excelBytes = analyticsService.exportAnalyticsToExcel(filter);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bluebells_school_analytics.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
