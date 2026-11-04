package com.bluebells.attendance;

import com.bluebells.attendance.dto.AttendanceCorrectionRequest;
import com.bluebells.attendance.dto.AttendanceRecordDto;
import com.bluebells.attendance.dto.AttendanceSummaryDto;
import com.bluebells.attendance.dto.RecordSectionAttendanceRequest;
import com.bluebells.common.ApiResponse;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.security.AuthorizationService;
import com.bluebells.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "Attendance", description = "Student attendance recording, audit corrections, and monthly summaries")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AuthorizationService authorizationService;

    public AttendanceController(AttendanceService attendanceService, AuthorizationService authorizationService) {
        this.attendanceService = attendanceService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('ATTENDANCE_RECORD')")
    @Operation(summary = "Mark daily attendance for a section in bulk (rejects duplicates)")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> recordAttendance(
            @Valid @RequestBody RecordSectionAttendanceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkAttendanceMarking(request.getSectionId(), principal);
        List<AttendanceRecordDto> saved = attendanceService.recordSectionAttendance(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance marked successfully for " + saved.size() + " students", saved));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get attendance history for current authenticated student")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getMyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {
        com.bluebells.students.Student student = authorizationService.getStudentForUser(principal.getId())
                .orElseThrow(() -> new com.bluebells.exception.ResourceNotFoundException("Student profile not found"));
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = (endDate != null) ? endDate : LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        List<AttendanceRecordDto> records = attendanceService.getStudentAttendanceHistory(student.getId(), start, end);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER')")
    @Operation(summary = "Get daily attendance for a section on a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getSectionAttendance(
            @PathVariable Long sectionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<AttendanceRecordDto> records = attendanceService.getSectionAttendance(sectionId, targetDate);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get student attendance history with row-level parent isolation check")
    public ResponseEntity<ApiResponse<List<AttendanceRecordDto>>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {

        authorizationService.checkStudentAccess(studentId, principal);

        LocalDate start = (startDate != null) ? startDate : LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = (endDate != null) ? endDate : LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        List<AttendanceRecordDto> records = attendanceService.getStudentAttendanceHistory(studentId, start, end);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/student/{studentId}/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get aggregate student attendance metrics (present, absent, late, percentage)")
    public ResponseEntity<ApiResponse<AttendanceSummaryDto>> getStudentSummary(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal) {

        authorizationService.checkStudentAccess(studentId, principal);

        LocalDate start = (startDate != null) ? startDate : LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = (endDate != null) ? endDate : LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        AttendanceSummaryDto summary = attendanceService.getStudentAttendanceSummary(studentId, start, end);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @PutMapping("/{id}/correction")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ATTENDANCE_CORRECT')")
    @Operation(summary = "Submit an attendance status correction with required audit justification")
    public ResponseEntity<ApiResponse<AttendanceRecordDto>> correctAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceCorrectionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AttendanceRecordDto updated = attendanceService.correctAttendance(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success("Attendance status corrected and audit logged", updated));
    }
}
