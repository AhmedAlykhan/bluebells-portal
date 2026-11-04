# Troubleshooting Guide — Bluebells School LMS

## Common Issues & Diagnoses

### 1. Java Version Mismatch
- **Symptom**: `class file has wrong version 61.0, should be 55.0` or Maven compiler failures.
- **Cause**: Spring Boot 3 requires Java 17+ bytecode.
- **Fix**: Set `JAVA_HOME=/opt/homebrew/opt/openjdk@17` and verify with `java -version`.

### 2. MySQL Connection Refused (`ERROR 2003 (HY000)`)
- **Symptom**: `Can't connect to MySQL server on '127.0.0.1:3306'`
- **Cause**: Homebrew MySQL service is stopped or listening on socket only.
- **Fix**: Start service with `brew services start mysql` or verify port with `netstat -an | grep 3306`.

### 3. Flyway Checksum Mismatch
- **Symptom**: `Migration checksum mismatch for migration version X`.
- **Cause**: An applied migration script was modified after being applied.
- **Fix**: In development, run `mvn flyway:repair` or reset the development database via `DROP DATABASE bluebells_school_db; CREATE DATABASE bluebells_school_db;`.

### 4. CORS Errors on Frontend
- **Symptom**: `Access to fetch at 'http://localhost:8080/api/v1/...' from origin 'http://localhost:5173' has been blocked by CORS policy`.
- **Cause**: Backend CORS configuration missing frontend origin.
- **Fix**: Check `SecurityConfig` and ensure `http://localhost:5173` is explicitly listed in `CorsConfiguration.setAllowedOrigins()`.
