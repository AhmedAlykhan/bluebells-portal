# Bluebells Schooling System — Production Readiness & Deployment Checklist

**System**: Bluebells Schooling System (Hyderabad, Sindh, Pakistan)  
**Target Environment**: Linux/macOS Ubuntu LTS / Debian / AWS / Bare Metal  
**JVM Version**: OpenJDK 17 LTS  
**Database**: MySQL 8.0 Enterprise / Percona Server  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com> (DevOps & Infrastructure Architect)  
**Date**: November 2026  

---

## 1. Readiness Assessment Summary

The Bluebells Schooling System has successfully completed all security audits, code reviews, database migrations, and regression testing. The checklist below certifies production readiness across all operational pillars.

---

## 2. Production Deployment Checklist

### 2.1. Environment Variables & Secrets Management
- [x] **Database Credentials**: Injected via environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`. No plaintext credentials stored in version control.
- [x] **JWT Secret**: Configured with a minimum 512-bit cryptographically secure key via `JWT_SECRET`.
- [x] **File Storage Path**: Configured via `STORAGE_UPLOAD_DIR` to a secure, persistent volume outside application root.

### 2.2. Database & Connection Pool Configuration
- [x] **HikariCP Pool**: Optimized settings:
  - `maximum-pool-size: 20`
  - `minimum-idle: 5`
  - `idle-timeout: 300000` (5 minutes)
  - `connection-timeout: 20000` (20 seconds)
  - `max-lifetime: 1800000` (30 minutes)
- [x] **Flyway Auto-Migration**: `spring.flyway.enabled: true`, `validate-on-migrate: false` (to support existing MySQL distributions).
- [x] **Charset & Timezone**: Strict `serverTimezone=Asia/Karachi` and `characterEncoding=utf8mb4`.

### 2.3. Web & Security Headers
- [x] **Content-Security-Policy (CSP)**: Configured to restrict frame embedding and script injection.
- [x] **X-Frame-Options**: `DENY` to prevent clickjacking attacks.
- [x] **X-Content-Type-Options**: `nosniff`.
- [x] **CORS Configuration**: Restricted to verified school production domain (`https://portal.bluebells.edu.pk`).
- [x] **Cookie Security**: `HttpOnly`, `Secure`, `SameSite=Strict`.

### 2.4. Logging, Auditing & Observability
- [x] **Spring Boot Actuator**: Health endpoint exposed at `/actuator/health` with database ping and disk space probes.
- [x] **Audit Trail**: Institutional mutations (admissions, fee payment reconciliation, certificate revocation, grade finalization) logged with actor username, timestamp, and client IP.
- [x] **Log Rotation**: Configured with Daily Rolling File Appender, retaining 30 days of compressed logs. Sensitive credentials masked from logs.

### 2.5. Disaster Recovery & Backup Strategy
- [x] **Automated Database Dumps**: Daily `mysqldump` with transaction consistency:
  ```bash
  mysqldump --single-transaction --quick --lock-tables=false \
    -u root -p bluebells_school_db | gzip > /backups/bluebells_$(date +%F).sql.gz
  ```
- [x] **Document & Evidence Storage Backup**: Daily incremental sync of `/storage/uploads/` to encrypted offsite object storage.

---

## 3. Production Deployment Runbook

```bash
# 1. Build Production Backend JAR
JAVA_HOME=/opt/homebrew/opt/openjdk@17 mvn clean package -DskipTests

# 2. Build Production Frontend Assets
cd frontend && npm run build && cd ..

# 3. Launch Backend with Production Profile
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=127.0.0.1
export DB_PORT=3306
export DB_NAME=bluebells_school_db
export DB_USER=bluebells_admin
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_512_bit_hex_encoded_secret_key

java -Xms1024m -Xmx2048m -jar backend/target/bluebells-lms-0.0.1-SNAPSHOT.jar
```

---

## 4. Final Sign-Off

The system meets all availability, security, and scalability criteria. **APPROVED FOR PRODUCTION DEPLOYMENT**.
