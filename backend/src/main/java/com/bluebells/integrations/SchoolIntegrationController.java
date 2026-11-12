package com.bluebells.integrations;

import com.bluebells.common.ApiResponse;
import com.bluebells.integrations.dto.SchoolIntegrationDto;
import com.bluebells.integrations.dto.TestIntegrationResponse;
import com.bluebells.integrations.dto.UpdateIntegrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integrations")
@Tag(name = "External Integrations", description = "SMS, WhatsApp, Online Payments, GPS, QR Attendance, and Push Notifications")
public class SchoolIntegrationController {

    private final SchoolIntegrationService integrationService;

    public SchoolIntegrationController(SchoolIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('INTEGRATIONS_MANAGE')")
    @Operation(summary = "List all external school integration connectors")
    public ResponseEntity<ApiResponse<List<SchoolIntegrationDto>>> getAllIntegrations() {
        List<SchoolIntegrationDto> list = integrationService.getAllIntegrations();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{key}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('INTEGRATIONS_MANAGE')")
    @Operation(summary = "Get integration connector details by key")
    public ResponseEntity<ApiResponse<SchoolIntegrationDto>> getIntegration(@PathVariable String key) {
        SchoolIntegrationDto dto = integrationService.getIntegrationByKey(key);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('INTEGRATIONS_MANAGE')")
    @Operation(summary = "Update integration status or credentials")
    public ResponseEntity<ApiResponse<SchoolIntegrationDto>> updateIntegration(
            @PathVariable String key,
            @Valid @RequestBody UpdateIntegrationRequest req) {
        SchoolIntegrationDto updated = integrationService.updateIntegration(key, req);
        return ResponseEntity.ok(ApiResponse.success("Integration updated", updated));
    }

    @PostMapping({"/{key}/test", "/{key}/ping"})
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('INTEGRATIONS_MANAGE')")
    @Operation(summary = "Test integration gateway connectivity and heartbeat")
    public ResponseEntity<ApiResponse<TestIntegrationResponse>> testIntegration(@PathVariable String key) {
        TestIntegrationResponse res = integrationService.testIntegrationConnectivity(key);
        return ResponseEntity.ok(ApiResponse.success("Connectivity test completed", res));
    }
}
