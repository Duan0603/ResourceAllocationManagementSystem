package com.company.resourcealloc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> checkHealth() {
        try {
            // Set query timeout to prevent blocking thread pool
            jdbcTemplate.setQueryTimeout(3);
            // Run a quick query to test database connectivity
            jdbcTemplate.execute("SELECT 1");
            return ResponseEntity.ok(Map.of("status", "UP", "database", "CONNECTED"));
        } catch (Exception e) {
            log.error("Health check database connection failed", e);
            return ResponseEntity.status(500).body(Map.of("status", "DOWN", "error", "Database connection check failed"));
        }
    }
}
