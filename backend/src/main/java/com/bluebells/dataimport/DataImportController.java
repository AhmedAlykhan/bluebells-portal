package com.bluebells.dataimport;

import com.bluebells.common.ApiResponse;
import com.bluebells.common.PageResponse;
import com.bluebells.dataimport.dto.*;
import com.bluebells.dataimport.entity.DataImportJob;
import com.bluebells.dataimport.service.DataImportExecutionService;
import com.bluebells.dataimport.service.DataImportPermissionService;
import com.bluebells.dataimport.service.DataImportValidationService;
import com.bluebells.dataimport.service.TemplateGenerationService;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/admin/data-import", "/api/admin/data-import"})
@Tag(name = "Bulk Data Import", description = "Secure bulk student and staff data import module")
public class DataImportController {

    private final TemplateGenerationService templateService;
    private final DataImportValidationService validationService;
    private final DataImportExecutionService executionService;
    private final DataImportPermissionService permissionService;
    private final UserRepository userRepository;

    public DataImportController(TemplateGenerationService templateService,
                                DataImportValidationService validationService,
                                DataImportExecutionService executionService,
                                DataImportPermissionService permissionService,
                                UserRepository userRepository) {
        this.templateService = templateService;
        this.validationService = validationService;
        this.executionService = executionService;
        this.permissionService = permissionService;
        this.userRepository = userRepository;
    }

    // =========================================================================
    // 1. TEMPLATES DOWNLOAD
    // =========================================================================

    @GetMapping("/templates/{type}.xlsx")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_DOWNLOAD_TEMPLATE', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Download official Excel import template (.xlsx) with instructions and validation dropdowns")
    public ResponseEntity<byte[]> downloadExcelTemplate(@PathVariable String type) {
        byte[] content;
        String filename;
        if ("staff".equalsIgnoreCase(type)) {
            content = templateService.generateStaffExcelTemplate();
            filename = "bluebells_staff_import_template.xlsx";
        } else {
            content = templateService.generateStudentExcelTemplate();
            filename = "bluebells_student_import_template.xlsx";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(content);
    }

    @GetMapping("/templates/{type}.csv")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_DOWNLOAD_TEMPLATE', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Download standard CSV import template (.csv) with headers and sample records")
    public ResponseEntity<byte[]> downloadCsvTemplate(@PathVariable String type) {
        byte[] content;
        String filename;
        if ("staff".equalsIgnoreCase(type)) {
            content = templateService.generateStaffCsvTemplate();
            filename = "bluebells_staff_import_template.csv";
        } else {
            content = templateService.generateStudentCsvTemplate();
            filename = "bluebells_student_import_template.csv";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(content);
    }

    // =========================================================================
    // 2. UPLOAD & VALIDATE
    // =========================================================================

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_UPLOAD', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Upload and stage an Excel or CSV file for student or staff bulk import")
    public ResponseEntity<ApiResponse<DataImportJobSummaryDto>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "importType", defaultValue = "STUDENT") String importType,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        DataImportJob job = validationService.createJob(file, importType, user, clientIp, userAgent);
        DataImportJobSummaryDto summary = executionService.getJobSummary(job.getJobId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("File uploaded and staged successfully. Ready for validation.", summary));
    }

    @PostMapping("/{jobId}/validate")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_VALIDATE', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Run server-side validation against database constraints and detect duplicates")
    public ResponseEntity<ApiResponse<DataImportPreviewDto>> validateJob(
            @PathVariable String jobId,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        DataImportPreviewDto preview = validationService.validateJob(jobId, clientIp, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Validation completed successfully.", preview));
    }

    // =========================================================================
    // 3. PREVIEW & ERRORS
    // =========================================================================

    @GetMapping("/{jobId}/preview")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_VIEW', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get paginated rows with validation status, filtered by status or search")
    public ResponseEntity<ApiResponse<DataImportPreviewDto>> getJobPreview(
            @PathVariable String jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) String search) {

        DataImportPreviewDto preview = validationService.getJobPreview(jobId, page, size, statusFilter, search);
        return ResponseEntity.ok(ApiResponse.success(preview));
    }

    @GetMapping("/{jobId}/errors")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_VIEW', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get list of all validation errors and warnings for a staged import job")
    public ResponseEntity<ApiResponse<List<DataImportErrorDto>>> getJobErrors(@PathVariable String jobId) {
        List<DataImportErrorDto> errors = validationService.getJobErrors(jobId);
        return ResponseEntity.ok(ApiResponse.success(errors));
    }

    // =========================================================================
    // 4. CONFIRM & CANCEL IMPORT
    // =========================================================================

    @PostMapping("/{jobId}/confirm")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_CONFIRM', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Confirm and commit validated student or staff records into live database")
    public ResponseEntity<ApiResponse<DataImportJobSummaryDto>> confirmImport(
            @PathVariable String jobId,
            @Valid @RequestBody(required = false) ConfirmImportRequest confirmRequest,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));

        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        DataImportJobSummaryDto summary = executionService.confirmImport(jobId, confirmRequest, user, clientIp, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Import executed successfully.", summary));
    }

    @PostMapping("/{jobId}/cancel")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_CANCEL', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Cancel a staged import job")
    public ResponseEntity<ApiResponse<Void>> cancelImport(
            @PathVariable String jobId,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        User user = userRepository.findById(principal.getId()).orElse(null);
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        executionService.cancelImport(jobId, user, clientIp, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Import job has been cancelled."));
    }

    // =========================================================================
    // 5. HISTORY & AUDIT & METRICS
    // =========================================================================

    @GetMapping("/history")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_VIEW_HISTORY', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get paginated history of all bulk data import jobs")
    public ResponseEntity<ApiResponse<PageResponse<DataImportJobSummaryDto>>> getHistory(
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<DataImportJobSummaryDto> page = executionService.getHistory(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/history/{jobId}")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_VIEW_HISTORY', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get detailed summary of an import job by jobId")
    public ResponseEntity<ApiResponse<DataImportJobSummaryDto>> getJobSummary(@PathVariable String jobId) {
        DataImportJobSummaryDto summary = executionService.getJobSummary(jobId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_VIEW', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get aggregated bulk data import dashboard KPI metrics")
    public ResponseEntity<ApiResponse<ImportStatisticsDto>> getStatistics() {
        ImportStatisticsDto stats = executionService.getStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/{jobId}/report")
    @PreAuthorize("hasAnyAuthority('DATA_IMPORT_DOWNLOAD_REPORT', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Download detailed CSV report of imported, skipped, and error records")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String jobId) {
        byte[] reportCsv = executionService.generateExecutionReportCsv(jobId);
        String filename = "import_report_" + jobId + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(reportCsv);
    }

    // =========================================================================
    // 6. PERMISSION MANAGEMENT (Super Admin Only)
    // =========================================================================

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "List eligible staff/leadership users and their active bulk import permissions")
    public ResponseEntity<ApiResponse<List<UserPermissionDto>>> getPermissions() {
        List<UserPermissionDto> users = permissionService.getEligibleUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping("/permissions/grant")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Grant bulk data import permissions to an authorized school leader")
    public ResponseEntity<ApiResponse<UserPermissionDto>> grantPermissions(
            @Valid @RequestBody PermissionGrantRequest grantRequest,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        User user = userRepository.findById(principal.getId()).orElse(null);
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        UserPermissionDto updated = permissionService.grantPermissions(grantRequest, user, clientIp, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Permissions granted successfully.", updated));
    }

    @PostMapping("/permissions/revoke")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Revoke bulk data import permissions from a user")
    public ResponseEntity<ApiResponse<UserPermissionDto>> revokePermissions(
            @Valid @RequestBody PermissionGrantRequest revokeRequest,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        User user = userRepository.findById(principal.getId()).orElse(null);
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        UserPermissionDto updated = permissionService.revokePermissions(revokeRequest, user, clientIp, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Permissions revoked successfully.", updated));
    }

    // =========================================================================
    // HELPER
    // =========================================================================

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
