package com.bluebells.identity;

import com.bluebells.common.ApiResponse;
import com.bluebells.common.PageResponse;
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
@RequestMapping("/api/v1/users")
@Tag(name = "User Administration", description = "User account administration and role provisioning")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Get paginated user accounts")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryDto>>> getUsers(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        PageResponse<UserSummaryDto> page = userService.getUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Provision a new user account with assigned roles")
    public ResponseEntity<ApiResponse<UserSummaryDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserSummaryDto created = userService.createUser(request);
        return new ResponseEntity<>(ApiResponse.success("User created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update user account status (ACTIVE, SUSPENDED, PENDING_ACTIVATION)")
    public ResponseEntity<ApiResponse<UserSummaryDto>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        UserSummaryDto updated = userService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", updated));
    }
}
