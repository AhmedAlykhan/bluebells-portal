package com.bluebells.dataimport.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PermissionGrantRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Permission codes are required")
    private List<String> permissionCodes;

    public PermissionGrantRequest() {
    }

    public PermissionGrantRequest(Long userId, List<String> permissionCodes) {
        this.userId = userId;
        this.permissionCodes = permissionCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }
}
