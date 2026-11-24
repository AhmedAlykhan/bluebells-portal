package com.bluebells.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class RootHealthController {

    @GetMapping({"/", "/health", "/api/v1/health"})
    public ResponseEntity<Map<String, Object>> rootHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Bluebells School Management System",
                "timestamp", Instant.now().toString()
        ));
    }
}
