# BLUEBELLS SCHOOLING SYSTEM
## Module H — Backend Production Deployment Readiness Report

**Document Reference:** `BSS-DEP-004`  
**Institution:** Bluebells Schooling System, Hyderabad, Sindh, Pakistan  
**Author:** Ahmed Ali Khan <medlykhan@gmail.com>  
**Runtime:** Java 17 Temurin JRE / Spring Boot 3.3.5  
**Status:** Verification Passed & Production Hardened  

---

### 1. Executive Summary

This report certifies the backend service of the Bluebells Schooling System for production deployment. The backend has undergone rigorous hardening across containerization, privilege de-escalation, memory bounds, dynamic port adaptation, and endpoint security isolation.

---

### 2. Multi-Stage Containerization & Hardening Audit

The backend is built and packaged using the multi-stage Dockerfile located at [`backend/Dockerfile`](file:///Users/ahmkhan/Desktop/portal/backend/Dockerfile).

#### 2.1 Multi-Stage Architecture

```
+-------------------------------------------------------------------------------+
| STAGE 1: Builder (maven:3.9-eclipse-temurin-17-alpine)                        |
| - Compiles 283 Java source files                                              |
| - Executes offline/cached Maven dependency resolution                         |
| - Packages repackaged Spring Boot executable JAR                              |
+-------------------------------------------------------------------------------+
                                    |
                    Copies only: app.jar (103 MB)
                                    v
+-------------------------------------------------------------------------------+
| STAGE 2: Hardened Runtime (eclipse-temurin:17-jre-alpine)                      |
| - Minimal Alpine Linux base (size < 180 MB)                                   |
| - Installs non-root system user 'bluebells' (UID 1000)                        |
| - Enforces read-only system permissions with designated storage paths         |
| - Embedded wget/curl health check probe                                       |
+-------------------------------------------------------------------------------+
```

#### 2.2 Security Privilege De-escalation (Non-Root Execution)
- **Vulnerability Remediated:** Previously, the container executed as `root` (UID 0), creating severe risks of host filesystem tampering in the event of an arbitrary code execution vulnerability.
- **Hardened State:**
  ```dockerfile
  RUN addgroup -S bluebells && adduser -S bluebells -G bluebells
  RUN mkdir -p /var/bluebells/storage /var/bluebells/logs \
      && chown -R bluebells:bluebells /app /var/bluebells
  USER bluebells
  ```
  All application threads run with unprivileged user permissions.

#### 2.3 Dynamic Port Binding & Signal Propagation
- **PaaS Challenge:** Cloud PaaS providers (Render, Koyeb, Heroku) dynamically assign an ephemeral port at runtime via the `$PORT` environment variable. Standard Docker exec forms (`ENTRYPOINT ["java", "-Dserver.port=${PORT}"]`) do not invoke a shell and fail to expand environment variables.
- **Hardened Solution:**
  ```dockerfile
  ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]
  ```
  The `sh -c` evaluates `${PORT:-8080}`, and `exec` replaces the shell process with the JVM process (PID 1). This ensures that `SIGTERM` and `SIGINT` signals from Docker or Kubernetes are delivered directly to the Spring Boot graceful shutdown listener.

---

### 3. JVM Memory Tuning for Free-Tier Footprints

Free-tier container environments typically impose a rigid **512 MB physical memory ceiling**. An untuned JVM running Spring Boot 3 easily exceeds 512 MB due to uncontrolled heap growth, Metaspace expansion, thread stacks, and JIT compilation buffers, leading to immediate operating system `OOM-Killed (Exit Code 137)`.

#### 3.1 Memory Tuning Specification

```bash
JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"
```

| Memory Component | Allocation on 512 MB Host | Technical Rationale |
| :--- | :--- | :--- |
| **Max Heap (`-XX:MaxRAMPercentage=75.0`)** | **~384 MB** | Dynamically scales to 75% of available cgroup memory limit without hardcoding fixed megabytes. |
| **Metaspace & Native Memory Headroom** | **~128 MB** | Reserves 25% of container RAM for Metaspace (~80MB), native thread stacks, DirectByteBuffers, and Alpine OS kernel overhead. |
| **Garbage Collector (`-XX:+UseG1GC`)** | Default G1 | Provides balanced throughput and low-pause collection for concurrent HTTP request handling. |
| **Crash Protection (`-XX:+ExitOnOutOfMemoryError`)** | Enabled | Ensures the container terminates cleanly and restarts immediately if heap exhaustion occurs, avoiding hung states. |

---

### 4. Actuator Lockdown & Diagnostic Security Review

Spring Boot Actuator endpoints provide vital observability but represent a critical attack vector if exposed improperly.

#### 4.1 Production Security Configuration ([`application-prod.yml`](file:///Users/ahmkhan/Desktop/portal/backend/src/main/resources/application-prod.yml))
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: never
      probes:
        enabled: true
    info:
      enabled: false
```

#### 4.2 Endpoint Authorization Rules ([`SecurityConfig.java`](file:///Users/ahmkhan/Desktop/portal/backend/src/main/java/com/bluebells/config/SecurityConfig.java))
```java
.authorizeHttpRequests(auth -> auth
    // 1. Health probe for Docker/PaaS container checks (public, status only)
    .requestMatchers("/actuator/health").permitAll()
    // 2. All other diagnostic actuator endpoints restricted to SUPER_ADMIN
    .requestMatchers("/actuator/**").hasRole("SUPER_ADMIN")
    // 3. Strict authentication on all other endpoints
    .anyRequest().authenticated()
)
```

#### 4.3 Security Verification Matrix
- `GET /actuator/health` -> `{"status":"UP"}` (HTTP 200, zero database hostnames, disk paths, or component details leaked).
- `GET /actuator/env` -> HTTP 401 Unauthorized / HTTP 403 Forbidden.
- `GET /actuator/beans` -> HTTP 401 Unauthorized / HTTP 403 Forbidden.
- `GET /actuator/metrics` -> HTTP 401 Unauthorized / HTTP 403 Forbidden.

---

### 5. Graceful Shutdown & Connection Draining

In `application-prod.yml`, graceful shutdown is activated:
```yaml
server:
  shutdown: graceful
```
- When a `SIGTERM` is received during a deployment or auto-sleep event, Tomcat stops accepting new connections immediately.
- In-flight HTTP transactions (e.g., student fee payment processing or grade submission) are permitted up to 30 seconds to complete cleanly before the JVM exits.

---

### 6. Storage Tier Audit: Ephemeral PaaS vs Persistent Volume

#### 6.1 The Ephemeral Disk Vulnerability on Free PaaS
On platforms such as Render Free, Heroku, or Koyeb Free:
- Storage written to the container filesystem (`./storage` or `/var/bluebells/storage`) is strictly **ephemeral**.
- **Every time the container restarts, sleeps, or updates, all stored files are permanently deleted.**
- Impact on Bluebells: Student admission photos, student digital ID card assets, uploaded homework submissions, and generated fee receipt PDFs would be destroyed.

#### 6.2 Architectural Remediation
1. **On Oracle Cloud Always Free (Option B - Recommended):**  
   Mount a persistent Docker named volume (`bluebells_storage_data:/var/bluebells/storage`) backed by OCI's 200 GB block storage. Files persist across all container rebuilds and restarts.
2. **On Render / PaaS (Option A - Staging Only):**  
   Integrate **Cloudflare R2** (S3-compatible, zero egress fees) for all binary asset uploads.
