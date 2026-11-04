package com.bluebells.staff;

import com.bluebells.common.ApiResponse;
import com.bluebells.common.PageResponse;
import com.bluebells.staff.dto.CreateStaffRequest;
import com.bluebells.staff.dto.StaffDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff Management", description = "Teacher and staff profiles and assignments")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Get paginated staff directory")
    public ResponseEntity<ApiResponse<PageResponse<StaffDto>>> getStaff(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        PageResponse<StaffDto> page = staffService.getStaff(pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Create staff profile linked to a user account")
    public ResponseEntity<ApiResponse<StaffDto>> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        StaffDto created = staffService.createStaff(request);
        return new ResponseEntity<>(ApiResponse.success("Staff profile created successfully", created), HttpStatus.CREATED);
    }
}
