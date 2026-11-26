package com.bluebells.certificates;

import com.bluebells.certificates.dto.*;
import com.bluebells.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/certificates")
@Tag(name = "Digital Certificates", description = "Digital certificate management, issuance, and public verification")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER') or hasAuthority('CERTIFICATES_VIEW')")
    @Operation(summary = "Get all active certificate templates")
    public ResponseEntity<ApiResponse<List<CertificateTemplateDto>>> getTemplates() {
        List<CertificateTemplateDto> templates = certificateService.getAllActiveTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @PostMapping("/templates")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('CERTIFICATES_MANAGE')")
    @Operation(summary = "Create or update certificate template")
    public ResponseEntity<ApiResponse<CertificateTemplateDto>> saveTemplate(@Valid @RequestBody CertificateTemplateDto dto) {
        CertificateTemplateDto saved = certificateService.saveTemplate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Template saved", saved));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER') or hasAuthority('CERTIFICATES_VIEW')")
    @Operation(summary = "List all issued certificates across school")
    public ResponseEntity<ApiResponse<List<IssuedCertificateDto>>> getAllCertificates() {
        List<IssuedCertificateDto> certs = certificateService.getAllIssuedCertificates();
        return ResponseEntity.ok(ApiResponse.success(certs));
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT') or hasAuthority('CERTIFICATES_VIEW')")
    @Operation(summary = "Get certificates issued to a specific student")
    public ResponseEntity<ApiResponse<List<IssuedCertificateDto>>> getStudentCertificates(@PathVariable Long studentId) {
        List<IssuedCertificateDto> certs = certificateService.getCertificatesForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(certs));
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('CERTIFICATES_MANAGE')")
    @Operation(summary = "Issue certificate to a student")
    public ResponseEntity<ApiResponse<IssuedCertificateDto>> issueCertificate(@Valid @RequestBody IssueCertificateRequest req) {
        IssuedCertificateDto issued = certificateService.issueCertificate(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Certificate issued successfully", issued));
    }

    @GetMapping("/verify/{code}")
    @Operation(summary = "Public verification endpoint for digital certificates")
    public ResponseEntity<ApiResponse<CertificateVerificationResponse>> verifyCertificate(@PathVariable String code) {
        CertificateVerificationResponse res = certificateService.verifyCertificate(code);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping("/{id}/revoke")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('CERTIFICATES_MANAGE')")
    @Operation(summary = "Revoke an issued certificate")
    public ResponseEntity<ApiResponse<Void>> revokeCertificate(
            @PathVariable Long id,
            @RequestParam String reason) {
        certificateService.revokeCertificate(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Certificate revoked", null));
    }
}
