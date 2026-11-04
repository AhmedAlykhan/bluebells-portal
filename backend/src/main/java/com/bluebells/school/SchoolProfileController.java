package com.bluebells.school;

import com.bluebells.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/school/profile")
@Tag(name = "School Profile", description = "Endpoints for managing institution profile and settings")
public class SchoolProfileController {

    private final SchoolProfileService schoolProfileService;

    public SchoolProfileController(SchoolProfileService schoolProfileService) {
        this.schoolProfileService = schoolProfileService;
    }

    @GetMapping
    @Operation(summary = "Get current school profile and settings")
    public ResponseEntity<ApiResponse<SchoolProfileDto>> getProfile() {
        SchoolProfileDto profile = schoolProfileService.getProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Update school profile and configuration")
    public ResponseEntity<ApiResponse<SchoolProfileDto>> updateProfile(@Valid @RequestBody SchoolProfileDto dto) {
        SchoolProfileDto updated = schoolProfileService.updateProfile(dto);
        return ResponseEntity.ok(ApiResponse.success("School profile updated successfully", updated));
    }
}
