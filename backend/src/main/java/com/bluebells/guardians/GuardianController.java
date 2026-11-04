package com.bluebells.guardians;

import com.bluebells.common.ApiResponse;
import com.bluebells.guardians.dto.GuardianDto;
import com.bluebells.guardians.dto.LinkGuardianRequest;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.dto.StudentDto;
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
@RequestMapping("/api/v1/guardians")
@Tag(name = "Guardians", description = "Guardian management and parent portal endpoints")
public class GuardianController {

    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @GetMapping("/my-children")
    @PreAuthorize("hasRole('PARENT')")
    @Operation(summary = "Get all students/children linked to the authenticated guardian (multi-child parent portal)")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getMyChildren(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<StudentDto> children = guardianService.getMyChildren(principal);
        return ResponseEntity.ok(ApiResponse.success(children));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Get guardian profile by ID")
    public ResponseEntity<ApiResponse<GuardianDto>> getGuardianById(@PathVariable Long id) {
        GuardianDto guardian = guardianService.getGuardianById(id);
        return ResponseEntity.ok(ApiResponse.success(guardian));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER')")
    @Operation(summary = "List all guardians linked to a specific student")
    public ResponseEntity<ApiResponse<List<GuardianDto>>> getGuardiansForStudent(@PathVariable Long studentId) {
        List<GuardianDto> guardians = guardianService.getGuardiansForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(guardians));
    }

    @PostMapping("/student/{studentId}/link")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Link an existing guardian to a student")
    public ResponseEntity<ApiResponse<Void>> linkGuardian(
            @PathVariable Long studentId,
            @Valid @RequestBody LinkGuardianRequest request) {
        guardianService.linkGuardian(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Guardian linked to student successfully"));
    }
}
