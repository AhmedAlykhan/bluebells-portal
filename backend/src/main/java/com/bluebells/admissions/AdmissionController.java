package com.bluebells.admissions;

import com.bluebells.admissions.dto.AdmissionApplicationDto;
import com.bluebells.admissions.dto.CreateApplicationRequest;
import com.bluebells.admissions.dto.EnrollApplicantRequest;
import com.bluebells.common.ApiResponse;
import com.bluebells.common.PageResponse;
import com.bluebells.students.dto.StudentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admissions/applications")
@Tag(name = "Admissions", description = "Online admission application and enrollment endpoints")
public class AdmissionController {

    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping
    @Operation(summary = "Submit a new admission application")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> submitApplication(
            @Valid @RequestBody CreateApplicationRequest request) {
        AdmissionApplicationDto created = admissionService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admission application submitted successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ADMISSION_MANAGE')")
    @Operation(summary = "List all admission applications with optional status filtering")
    public ResponseEntity<ApiResponse<PageResponse<AdmissionApplicationDto>>> getApplications(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdmissionApplicationDto> page = admissionService.getApplications(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ADMISSION_MANAGE')")
    @Operation(summary = "Get admission application details by ID")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> getApplicationById(@PathVariable Long id) {
        AdmissionApplicationDto app = admissionService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(app));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ADMISSION_MANAGE')")
    @Operation(summary = "Update admission application status")
    public ResponseEntity<ApiResponse<AdmissionApplicationDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String notes = body.get("notes");
        AdmissionApplicationDto updated = admissionService.updateStatus(id, status, notes);
        return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", updated));
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('ADMISSION_MANAGE')")
    @Operation(summary = "Enroll an accepted applicant as a registered student")
    public ResponseEntity<ApiResponse<StudentDto>> enrollApplicant(
            @PathVariable Long id,
            @Valid @RequestBody EnrollApplicantRequest request) {
        StudentDto student = admissionService.enrollApplicant(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Applicant enrolled successfully as student " + student.getAdmissionNumber(), student));
    }
}
