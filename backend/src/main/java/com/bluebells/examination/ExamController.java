package com.bluebells.examination;

import com.bluebells.common.ApiResponse;
import com.bluebells.examination.dto.CreateExamRequest;
import com.bluebells.examination.dto.ExamDto;
import com.bluebells.examination.dto.MarksEntryDto;
import com.bluebells.examination.dto.RecordMarksRequest;
import com.bluebells.examination.dto.ReportCardDto;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.security.AuthorizationService;
import com.bluebells.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@Tag(name = "Examinations", description = "Exam schedules, subject marks recording, result publishing, and report cards")
public class ExamController {

    private final ExamService examService;
    private final AuthorizationService authorizationService;

    public ExamController(ExamService examService, AuthorizationService authorizationService) {
        this.examService = examService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Create an examination schedule")
    public ResponseEntity<ApiResponse<ExamDto>> createExam(@Valid @RequestBody CreateExamRequest request) {
        ExamDto exam = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam schedule created successfully", exam));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "List all examinations")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getExams() {
        List<ExamDto> exams = examService.getExams();
        return ResponseEntity.ok(ApiResponse.success(exams));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get exam details by ID")
    public ResponseEntity<ApiResponse<ExamDto>> getExamById(@PathVariable Long id) {
        ExamDto exam = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.success(exam));
    }

    @PostMapping("/{id}/marks")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('MARKS_ENTRY')")
    @Operation(summary = "Record subject marks for students in bulk")
    public ResponseEntity<ApiResponse<List<MarksEntryDto>>> recordMarks(
            @PathVariable Long id,
            @Valid @RequestBody RecordMarksRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        if (request.getExamId() == null) {
            request.setExamId(id);
        }
        List<MarksEntryDto> entries = examService.recordMarks(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Marks recorded successfully for " + entries.size() + " students", entries));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('RESULTS_PUBLISH')")
    @Operation(summary = "Publish exam results and generate student report cards")
    public ResponseEntity<ApiResponse<ExamDto>> publishResults(@PathVariable Long id) {
        ExamDto exam = examService.publishExamResults(id);
        return ResponseEntity.ok(ApiResponse.success("Exam results published and report cards generated successfully", exam));
    }

    @GetMapping("/my-report-cards")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get all published report cards for the authenticated student")
    public ResponseEntity<ApiResponse<List<ReportCardDto>>> getMyReportCards(
            @AuthenticationPrincipal UserPrincipal principal) {
        com.bluebells.students.Student student = authorizationService.getStudentForUser(principal.getId())
                .orElseThrow(() -> new com.bluebells.exception.ResourceNotFoundException("Student profile not found"));
        List<ReportCardDto> cards = examService.getReportCardsForStudent(student.getId());
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @GetMapping("/students/{studentId}/report-cards")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get all report cards for a student with object-level authorization")
    public ResponseEntity<ApiResponse<List<ReportCardDto>>> getStudentReportCards(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkStudentAccess(studentId, principal);
        List<ReportCardDto> cards = examService.getReportCardsForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @GetMapping("/{id}/report-cards/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get published report card for student with row-level parent isolation check")
    public ResponseEntity<ApiResponse<ReportCardDto>> getReportCard(
            @PathVariable Long id,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkStudentAccess(studentId, principal);
        ReportCardDto reportCard = examService.getReportCard(id, studentId);
        return ResponseEntity.ok(ApiResponse.success(reportCard));
    }
}
