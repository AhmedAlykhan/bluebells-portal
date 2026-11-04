# Bluebells Schooling System – Secure Bulk Student and Staff Data Import Module
## Comprehensive Technical Documentation and Operational Manual

---

## 1. Executive Summary & Module Overview

The **Bulk Data Import Module** for the **Bluebells Schooling System** provides authorized school leadership (Super Administrators, Academic Principal **Huma Arif**, and Vice Principal **Hana Khan**) with an enterprise-grade, secure, multi-stage mechanism to onboard student and faculty records at scale.

Rather than executing raw, risky database insertions or exposing unverified endpoints, this system enforces a **Zero-Trust Multi-Stage Import Pipeline**:
1. **Dynamic Template Generation**: Context-aware Excel (`.xlsx`) and CSV (`.csv`) templates with live dropdown validations sourced in real time from the active database.
2. **Strict File Pre-Flight & Sanitization**: Formula injection mitigation (neutralizing spreadsheet formula prefixes `=,+,-,@,\t,\r`), file size enforcement (15MB max), and format parsing.
3. **In-Memory Validation Engine**: Multi-tier row inspection checking format syntax (Pakistani CNIC `XXXXX-XXXXXXX-X`, phone numbers `03XX-XXXXXXX`, email), required headers, and foreign key references (classes, sections, sessions, roles, subjects).
4. **Interactive Staging & Reconciliation UI**: Visual breakdown of records by status (`VALID`, `INVALID`, `DUPLICATE`, `WARNING`), live cell-level error tooltips, downloadable CSV error reports, and job cancellation capabilities.
5. **Dual Execution Engine**:
   - **Strict Mode**: Fails the entire batch atomically if a single error or duplicate exists.
   - **Import Valid Only Mode**: Atomically commits valid records while skipping duplicates and invalid rows with comprehensive audit logging.
6. **Immutable Audit Ledger**: Comprehensive tracking of every template download, file upload, validation pass, import confirmation, job cancellation, and permission delegation.

---

## 2. System Architecture & Workflow

The module operates across a decoupled Spring Boot backend architecture and a responsive React frontend interface:

```
[ School Leadership (Browser) ]
              │
              ▼  (JWT Auth + Permission Guard)
[ Spring Boot DataImportController ]
  (/api/v1/admin/data-import/*)
              │
   ┌──────────┴───────────────────────────────────────┐
   │                                                  │
   ▼                                                  ▼
[ TemplateGenerationService ]               [ DataImportValidationService ]
 - Queries Live Classes, Sections,             - Apache POI (.xlsx) & Commons CSV (.csv)
   Sessions, Roles, Subjects                   - Formula Injection Neutralizer
 - Generates Locked Reference Sheets           - Duplicate Detection (In-File & DB)
 - Applies Excel DataValidation Dropdowns      - Persists Staged Rows & Errors
   │                                                  │
   └──────────────────────────┬───────────────────────┘
                              │
                              ▼
                [ DataImportExecutionService ]
                 - Evaluates Mode (Strict vs Valid Only)
                 - Auto-links/Deduplicates Guardians
                 - Enrolls Students in Academic Sessions
                 - Creates Staff Users, BCrypt Passwords, Roles
                 - Assigns Teaching Subjects & Classes
                              │
                              ▼
                 [ DataImportAuditService ]
                 - Writes to `data_import_audit_logs`
```

---

## 3. Database Schema & Data Models

The system is backed by five dedicated tables designed and migrated via Flyway (`V5__bulk_data_import_schema.sql` and `V6__user_direct_permissions.sql`):

### 3.1 `data_import_jobs`
Represents an import batch from upload through confirmation or cancellation.
- `id` (BIGINT, PK, Auto-increment)
- `job_type` (VARCHAR(50)): `STUDENT` or `STAFF`
- `status` (VARCHAR(50)): `PENDING`, `VALIDATING`, `VALIDATED`, `IMPORTING`, `COMPLETED`, `PARTIALLY_COMPLETED`, `FAILED`, `CANCELLED`
- `file_name` (VARCHAR(255)): Original name of uploaded document
- `file_size` (BIGINT): File size in bytes
- `file_type` (VARCHAR(50)): `XLSX` or `CSV`
- `storage_path` (VARCHAR(500)): Local file storage reference
- `total_rows` (INT): Total non-header rows identified
- `valid_rows` (INT): Rows passing all schema, reference, and syntax checks
- `invalid_rows` (INT): Rows with validation errors
- `duplicate_rows` (INT): Rows matching existing DB records or repeated within the batch
- `warning_rows` (INT): Rows with non-critical warnings
- `imported_rows` (INT): Successfully persisted entities
- `skipped_rows` (INT): Skipped records (in Valid Only mode)
- `execution_mode` (VARCHAR(50)): `STRICT` or `VALID_ONLY`
- `uploaded_by` (BIGINT, FK -> `users.id`): Initiating administrator
- `confirmed_by` (BIGINT, FK -> `users.id`): Administrator who authorized execution
- `created_at` / `completed_at` (TIMESTAMP)

### 3.2 `data_import_job_rows`
Staged row-level data parsed from the upload file.
- `id` (BIGINT, PK, Auto-increment)
- `job_id` (BIGINT, FK -> `data_import_jobs.id`)
- `` `row_number` `` (INT): Excel row number (1-indexed matching file)
- `sheet_name` (VARCHAR(100)): Target worksheet name
- `raw_data` (LONGTEXT / JSON): Sanitized key-value row payload
- `entity_identifier` (VARCHAR(100)): Primary unique key (`admissionNumber` or `staffCnic`/`employeeCode`)
- `status` (VARCHAR(50)): `VALID`, `INVALID`, `DUPLICATE`, `WARNING`, `IMPORTED`, `SKIPPED`, `FAILED`
- `created_at` (TIMESTAMP)

### 3.3 `data_import_errors`
Detailed error and warning diagnostics linked to specific rows and fields.
- `id` (BIGINT, PK, Auto-increment)
- `job_id` (BIGINT, FK -> `data_import_jobs.id`)
- `` `row_number` `` (INT): File row number
- `field_name` (VARCHAR(100)): Culprit column header
- `field_value` (VARCHAR(500)): Raw input value
- `error_message` (TEXT): Human-readable remediation instruction
- `error_type` (VARCHAR(50)): `REQUIRED`, `FORMAT`, `DUPLICATE`, `FOREIGN_KEY`, `SECURITY`
- `created_at` (TIMESTAMP)

### 3.4 `data_import_audit_logs`
Immutable audit trail capturing all administrative operations.
- `id` (BIGINT, PK, Auto-increment)
- `job_id` (BIGINT, Nullable, FK -> `data_import_jobs.id`)
- `user_id` (BIGINT, FK -> `users.id`)
- `username` (VARCHAR(100))
- `action` (VARCHAR(100)): e.g. `UPLOAD`, `VALIDATE`, `CONFIRM`, `CANCEL`, `DOWNLOAD_TEMPLATE`, `GRANT_PERMISSION`, `REVOKE_PERMISSION`
- `details` (TEXT): Contextual JSON details including row counts and execution mode
- `ip_address` (VARCHAR(100))
- `created_at` (TIMESTAMP)

### 3.5 `user_permissions`
Granular user-level permission mapping table enabling Super Admins to delegate specific capabilities to the Principal and Vice Principal without modifying system-wide core roles.
- `user_id` (BIGINT, FK -> `users.id`)
- `permission_id` (BIGINT, FK -> `permissions.id`)
- Primary Key: (`user_id`, `permission_id`)

---

## 4. API Endpoints Specification

All endpoints are hosted under `/api/v1/admin/data-import` (with legacy compatibility under `/api/admin/data-import`). All endpoints enforce Bearer JWT authentication and Spring Security `@PreAuthorize` method annotations.

| Method | Endpoint | Required Permission | Description |
|---|---|---|---|
| `GET` | `/templates/{type}` | `DATA_IMPORT_DOWNLOAD_TEMPLATE` | Downloads official `.xlsx` or `.csv` template with live reference dropdowns. `type` = `student` or `staff`. Format queried via `?format=xlsx` or `?format=csv`. |
| `POST` | `/upload` | `DATA_IMPORT_UPLOAD` | Multipart upload (`file`, `type`). Sanitizes formula injection, validates schema, checks duplicates, and stages rows. |
| `GET` | `/jobs/{jobId}/preview` | `DATA_IMPORT_VIEW` | Fetches validation summary, row statuses, and field-level error messages. |
| `POST` | `/jobs/{jobId}/confirm` | `DATA_IMPORT_CONFIRM` | Executes import into authoritative tables. Accepts JSON body `{ "mode": "STRICT" \| "VALID_ONLY" }`. |
| `POST` | `/jobs/{jobId}/cancel` | `DATA_IMPORT_CANCEL` | Aborts a staged job, deletes uploaded file, updates status to `CANCELLED`. |
| `GET` | `/jobs/{jobId}/errors/export` | `DATA_IMPORT_DOWNLOAD_REPORT` | Streams a downloadable CSV detailing all validation errors for remediation. |
| `GET` | `/history` | `DATA_IMPORT_VIEW_HISTORY` | Retrieves historical paginated import jobs with execution metrics. |
| `GET` | `/statistics` | `DATA_IMPORT_VIEW` | Returns global KPI metrics (total imported, successful, failed, duplicates). |
| `GET` | `/permissions` | Super Admin Only | Lists leadership users (`Huma Arif`, `Hana Khan`) and their direct import permissions. |
| `POST` | `/permissions/grant` | Super Admin Only | Grants specific permission code to a user ID. |
| `POST` | `/permissions/revoke` | Super Admin Only | Revokes specific permission code from a user ID. |

---

## 5. Security Architecture & Threat Model

### 5.1 Formula Injection (CSV / Excel Injection / DDE)
Spreadsheet applications (Microsoft Excel, LibreOffice Calc, Apple Numbers) treat cells starting with `=`, `+`, `-`, `@`, `\t`, or `\r` as executable formulas or Dynamic Data Exchange (DDE) commands. If an attacker submits a name like `=cmd|' /C calc'!A0`, standard export/display can trigger remote code execution on administrative workstations.
- **Remediation**: The `DataImportValidationService` inspects every parsed string cell. Any cell whose first character matches the dangerous prefix set is immediately prefixed with a single quotation mark (`'`), neutralizing formula execution while preserving the text value.
- **Storage**: Sanitized values are stored directly in both `data_import_job_rows.raw_data` and final production entities (`students.first_name`, etc.).

### 5.2 Granular RBAC & Least Privilege
The system defines 8 fine-grained permissions:
- `DATA_IMPORT_VIEW`
- `DATA_IMPORT_DOWNLOAD_TEMPLATE`
- `DATA_IMPORT_UPLOAD`
- `DATA_IMPORT_VALIDATE`
- `DATA_IMPORT_CONFIRM`
- `DATA_IMPORT_VIEW_HISTORY`
- `DATA_IMPORT_DOWNLOAD_REPORT`
- `DATA_IMPORT_CANCEL`

**Role Access Matrix**:
- **Super Administrator**: Full authority to upload, validate, execute, cancel, view history, and delegate/revoke permissions.
- **Academic Principal (Huma Arif)**: Institutional management; granted import capabilities dynamically by Super Admin.
- **Vice Principal (Hana Khan)**: Operational leadership; granted import capabilities dynamically by Super Admin.
- **Teachers, Students, Parents, Accountants**: **Strictly Denied (403 Forbidden)** at the controller, filter, and UI layer. Navigation links are hidden from their portals.

---

## 6. Entity Relationship Mapping & Data Persistence

### 6.1 Student Import Flow
When a student record is committed:
1. **Guardian Deduplication**:
   - Primary Phone or Guardian CNIC is checked against the `guardians` table.
   - If a guardian exists, the student is linked via `guardian_student_links`.
   - If not found, a new `Guardian` record is created and linked.
2. **Student Entity**:
   - Creates `Student` record with `admission_number`, `first_name`, `last_name`, `date_of_birth`, `gender`, `blood_group`, `emergency_contact`.
3. **Enrollment**:
   - Resolves `ClassEntity` and `Section` by name.
   - Creates an active `Enrollment` in the specified `AcademicSession` with roll number assigned.

### 6.2 Staff Import Flow
When a staff record is committed:
1. **User Account Creation**:
   - Creates a portal `User` record with generated username (e.g. `firstname.lastname` or employee code).
   - Generates a secure BCrypt password (defaulting to temporary password `Bluebells@2026`).
   - Assigns corresponding Spring Security role (e.g. `ROLE_TEACHER`).
2. **Staff Entity**:
   - Creates `Staff` record linked to `User`.
   - Populates qualification, designation, CNIC, employment date, emergency contacts.
3. **Teacher Assignment**:
   - If role is Teacher and target class/subject are provided, creates active `TeacherAssignment` mappings.

---

## 7. Administrative Operational Guide

### 7.1 Accessing the Import Portal
1. Log in to Bluebells Portal as Super Admin or authorized Principal / Vice Principal.
2. In the left navigation menu, expand **Administration → Data Management** and click **Bulk Data Import**.
3. If unauthorized, a warning banner explains that Super Admin delegation is required.

### 7.2 Executing an Import Batch
1. **Step 1: Download Official Template**:
   - Click **Download Excel Template (.xlsx)** under either Students or Staff.
   - Note the second tab ("Reference Data") containing available classes, sections, and fee categories.
2. **Step 2: Populate Data**:
   - Use Excel or CSV. Utilize dropdown selections for Classes, Sections, and Genders.
   - Ensure admission numbers and CNICs follow standard formats.
3. **Step 3: Upload & Validation**:
   - Drag and drop or browse the completed file into the upload zone.
   - The validation engine automatically processes rows and displays real-time statistics.
4. **Step 4: Review Preview Table**:
   - Filter rows using pills: `All`, `Valid`, `Invalid`, `Duplicate`, `Warnings`.
   - Click on invalid rows to see specific error messages (e.g. "Class 'Grade 15' does not exist").
   - Click **Download Error Report (CSV)** if corrections need to be made offline.
5. **Step 5: Execution Mode Selection**:
   - Choose **Strict Mode** (rejects if any error exists) or **Import Valid Only** (commits valid rows, skips duplicates).
   - Click **Commit Import to Database**.
6. **Step 6: Confirmation & Verification**:
   - View execution summary with counts of imported, skipped, and failed records.
   - Use direct links to verify newly imported records in the **Student Directory** or **Staff Directory**.

---

## 8. Troubleshooting & Common Scenarios

| Issue | Root Cause | Remediation |
|---|---|---|
| **403 Forbidden on Upload** | User lacks `DATA_IMPORT_UPLOAD` permission | Have Super Admin navigate to the "Permission Control" tab and grant the permission. |
| **"Class not found" error** | Class name in file does not match active DB records | Open the "Reference Data" sheet in the template and copy the exact class name (e.g. `Grade 10`). |
| **"Invalid CNIC format"** | CNIC missing dashes or has incorrect digit count | Ensure format is exactly 13 digits separated by hyphens: `41302-1234567-1`. |
| **"Duplicate Admission Number"** | Admission number already assigned to an enrolled student | Check Student Directory or assign the next available sequential admission number. |
| **Formula Injection Warning** | Cell begins with `=`, `+`, `-`, or `@` | System automatically sanitizes input by prepending `'`. To remove, ensure data is entered as plain text. |
