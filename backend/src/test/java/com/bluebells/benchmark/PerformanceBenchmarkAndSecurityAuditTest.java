package com.bluebells.benchmark;

import com.bluebells.academics.AcademicSession;
import com.bluebells.academics.AcademicSessionRepository;
import com.bluebells.academics.ClassEntity;
import com.bluebells.academics.ClassRepository;
import com.bluebells.academics.Section;
import com.bluebells.academics.SectionRepository;
import com.bluebells.finance.FeeCategory;
import com.bluebells.finance.FeeCategoryRepository;
import com.bluebells.finance.dto.CreateFeeStructureRequest;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.reports.CsvSanitizer;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PerformanceBenchmarkAndSecurityAuditTest {

    private static final Logger log = LoggerFactory.getLogger(PerformanceBenchmarkAndSecurityAuditTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AcademicSessionRepository sessionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private FeeCategoryRepository categoryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;

    @BeforeEach
    void setUp() {
        User admin = userRepository.findByUsername("admin").orElseThrow();
        adminToken = jwtTokenProvider.generateToken(UserPrincipal.create(admin));
    }

    @Test
    @DisplayName("Block 10: Performance Benchmark - p95 Latency < 300ms SLA for Dashboard and Core Reads")
    void testReadLatencySla() throws Exception {
        int iterations = 30;
        List<Long> latencies = new ArrayList<>();

        // Warmup
        mockMvc.perform(get("/api/v1/reports/dashboard")
                .header("Authorization", "Bearer " + adminToken));

        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            mockMvc.perform(get("/api/v1/reports/dashboard")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)));
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            latencies.add(elapsedMs);
        }

        Collections.sort(latencies);
        int p95Index = (int) Math.ceil(0.95 * latencies.size()) - 1;
        long p95Ms = latencies.get(p95Index);
        double avgMs = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);

        log.info("[BENCHMARK SLA] Read iterations: {}, avg: {} ms, p95: {} ms", iterations, String.format("%.2f", avgMs), p95Ms);

        // Strict SLA: p95 must be under 300ms on local test environment
        assertTrue(p95Ms < 300, "p95 read latency must be < 300ms, actual: " + p95Ms + "ms");
    }

    @Test
    @DisplayName("Block 10: Security Audit - SQL Injection Payloads Handled Safely Without Execution")
    void testSqlInjectionResilience() throws Exception {
        // Attempt SQL injection in search and URL params
        String[] payloads = {
                "' OR '1'='1",
                "1; DROP TABLE users; --",
                "' UNION SELECT username, password_hash FROM users --",
                "admin' --"
        };

        for (String payload : payloads) {
            // Searching students with injection payload should safely return empty results or 400, never 500 or SQL error
            mockMvc.perform(get("/api/v1/students")
                            .param("query", payload)
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)));
        }
    }

    @Test
    @DisplayName("Block 10: Security Audit - Formula Injection CWE-1236 Sanitization Matrix")
    void testFormulaInjectionSanitizationMatrix() {
        String[] injectionAttacks = {
                "=cmd|' /C calc'!A0",
                "+1+1",
                "-2+3*4",
                "@SUM(A1:A10)",
                "\t=HYPERLINK(\"http://malicious.com\")",
                "\r+2+2"
        };

        for (String attack : injectionAttacks) {
            String sanitized = CsvSanitizer.sanitize(attack);
            // Must begin with quote followed by single quote "'"
            assertTrue(sanitized.startsWith("\"'") || sanitized.startsWith("'"),
                    "Attack '" + attack + "' must be escaped with prepended single quote, got: " + sanitized);
        }
    }
}
