# Bluebells Schooling System — Role-Based Access Control (RBAC) & Permission Matrix

**Campus**: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan (Postal Code: 71800)  
**Academic Session**: 2026–2027  
**Document Classification**: Confidential / Security Architecture Specification  
**Version**: 1.0.0 (Release-Candidate Hardened)

---

## 1. Security Architecture & Core Tenets

The Bluebells School LMS & School Management Portal implements a defense-in-depth authorization framework designed to eliminate unauthorized data access, privilege escalation, and Insecure Direct Object Reference (IDOR) vulnerabilities across all academic, administrative, and financial modules.

### Core Principles
1. **Deny by Default / Fail Closed**: In the absence of an explicit permission or ownership link, requests are rejected immediately with HTTP `403 Forbidden`.
2. **Two-Tier Enforcement**:
   - **Tier 1 (Route-Level RBAC)**: Spring Security `@PreAuthorize("hasAnyRole(...)")` on controller methods + React `ProtectedRoute` wrappers on the frontend.
   - **Tier 2 (Object-Level Authorization & IDOR Defense)**: `AuthorizationService` programmatic evaluation of biological (Parent-Ward), professional (Teacher-Section), or identity (Student-Self) ownership in JPA transactional layers.
3. **Data Segregation**:
   - **Teachers** are strictly segregated from fee structures, tuition challans, and collection ledgers.
   - **Accountants** have zero authority to alter grades, attendance logs, or academic curricula.
   - **Students** operate strictly in a self-service scope (`/me`, `/my`), with all administrative panels stripped from both the navigation shell and the backend routing tree.
   - **Parents** can query only their legally linked children (verified against national CNIC and `parent_student` records).

---

## 2. Institutional Roles

| Role Identifier | Institutional Persona | Scope & Responsibility |
|---|---|---|
| `ROLE_SUPER_ADMIN` | System Architect / Campus Director | Global campus oversight, school configuration, database backups, audit logs, and master user provisioning. |
| `ROLE_SCHOOL_ADMIN` | Campus Registrar / Admissions Office | Operational campus management, admissions processing, student enrollment, and scheduling. |
| `ROLE_PRINCIPAL` | Academic Principal (Huma Arif) | Faculty leadership, academic standards, exam approvals, transcript signing, and teacher allocations. |
| `ROLE_TEACHER` | Subject Faculty (e.g., Sir Ahmed, Miss Fatima) | Classroom syllabus, attendance marking for assigned periods, homework assignments, and exam grading. |
| `ROLE_ACCOUNTANT` | Chief Accountant (Syed Tariq) | Fee structures, monthly tuition challans, bank reconciliations, payment collection entries, and financial ledgers. |
| `ROLE_PARENT` | Guardian (e.g., Mohammad Tariq Khan) | Real-time monitoring of enrolled children (attendance, fee challans, exam results, timetable). |
| `ROLE_STUDENT` | Enrolled Scholar (e.g., Ali Khan, 9-A) | Personal student portal: viewing personal timetable, attendance percentage, coursework submissions, report cards, and fee vouchers. |

---

## 3. Comprehensive Permission Matrix

The following matrix delineates permissions across all operational modules of the portal.

**Legend**:
- **FULL**: Full CRUD (Create, Read, Update, Delete)
- **RO**: Read-Only across the campus
- **ASSIGNED**: CRUD or Read restricted strictly to assigned classes/subjects
- **SELF**: Read-Only restricted strictly to personal records
- **WARD**: Read-Only restricted strictly to verified child/ward records
- **DENY**: No access (HTTP 403 / Hidden in UI)

| Module / Resource | SUPER_ADMIN | SCHOOL_ADMIN | PRINCIPAL | TEACHER | ACCOUNTANT | PARENT | STUDENT |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **Dashboard** | Campus KPI | Campus KPI | Campus KPI | Teacher Workspace | Finance Workspace | Parent Portal | Student Portal |
| **School Settings & Audit** | FULL | RO | RO | DENY | DENY | DENY | DENY |
| **Academics & Curricula** | FULL | FULL | FULL | ASSIGNED (RO) | DENY | DENY | DENY |
| **Admissions & Registration** | FULL | FULL | RO | DENY | DENY | DENY | DENY |
| **Student Directory** | FULL | FULL | FULL | ASSIGNED (RO) | RO (Billing) | DENY | DENY |
| **Timetable & Schedules** | FULL | FULL | FULL | ASSIGNED (RO) | DENY | WARD (RO) | SELF (RO) |
| **Attendance (Marking)** | FULL | FULL | FULL | ASSIGNED | DENY | DENY | DENY |
| **Attendance (Viewing)** | FULL | FULL | FULL | ASSIGNED | DENY | WARD (RO) | SELF (RO) |
| **LMS & Coursework** | FULL | FULL | FULL | ASSIGNED (FULL)| DENY | WARD (RO) | SELF (RO) |
| **Examinations & Grading** | FULL | FULL | FULL | ASSIGNED (Grade)| DENY | DENY | DENY |
| **Report Cards & Transcripts**| FULL | FULL | FULL | ASSIGNED (RO) | DENY | WARD (RO) | SELF (RO) |
| **Fee Structures & Policies** | FULL | FULL | RO | DENY | FULL | DENY | DENY |
| **Fee Challans & Payments** | FULL | FULL | RO | **DENY** | FULL | WARD (RO) | SELF (RO) |
| **Financial Ledger & Receipts**| FULL | FULL | RO | **DENY** | FULL | DENY | DENY |
| **Reports & BI Analytics** | FULL | FULL | FULL | DENY | Financial Only | DENY | DENY |

---

## 4. Object-Level Ownership & IDOR Protection Rules

Backend enforcement is implemented in `com.bluebells.security.AuthorizationService`:

### 4.1 Student Self-Ownership Rule
```java
public void checkStudentAccess(Long studentId, UserPrincipal principal) {
    if (isAdmin(principal) || isPrincipal(principal)) return;

    if (isStudent(principal)) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if (!student.getUser().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Access denied: You may only view your own student records.");
        }
        return;
    }
    // ...
}
```
- **Guaranteed Isolation**: When a student requests `GET /api/v1/students/{id}`, `GET /api/v1/attendance/student/{id}`, `GET /api/v1/finance/student/{id}/invoices`, or `GET /api/v1/exams/students/{id}/report-cards`, the backend checks `student.user.id == principal.id`.
- Attempts to query another student's ID return `403 Forbidden`.
- Convenience self-service endpoints (`GET /api/v1/students/me`, `GET /api/v1/attendance/my`, `GET /api/v1/finance/my-invoices`, `GET /api/v1/exams/my-report-cards`, `GET /api/v1/timetable/my`) automatically resolve the caller's authenticated user ID.

### 4.2 Parent Ward Boundary Rule
```java
if (isParent(principal)) {
    boolean isLinked = parentStudentRepository.existsByParentUserIdAndStudentId(principal.getId(), studentId);
    if (!isLinked) {
        throw new AccessDeniedException("Access denied: You are not authorized to view records for this student.");
    }
    return;
}
```
- A parent can only view attendance, fee challans, and academic transcripts for students officially linked in `parent_student` mapping.
- Crossing ward boundaries triggers an audit alert and `403 Forbidden`.

### 4.3 Teacher Classroom Scope Rule
```java
if (isTeacher(principal)) {
    Teacher teacher = teacherRepository.findByUserId(principal.getId())
        .orElseThrow(() -> new AccessDeniedException("Teacher profile not found"));
    boolean teachesStudent = enrollmentRepository.existsByStudentIdAndClassSections(studentId, teacher.getId());
    if (!teachesStudent) {
        throw new AccessDeniedException("Access denied: Student is not enrolled in any of your assigned classes.");
    }
    return;
}
```
- Teachers can only submit attendance marks and grade exam papers for students enrolled in sections they are actively assigned to teach.
- Unassigned teacher attempts to mark attendance or alter marks are rejected.

### 4.4 Financial Ledger Segregation Rule
```java
public void checkFeeAccess(Long studentId, UserPrincipal principal) {
    if (isTeacher(principal)) {
        throw new AccessDeniedException("Teachers are strictly restricted from accessing student financial records.");
    }
    if (isAdmin(principal) || isAccountant(principal)) return;
    
    // Fall back to student self-ownership or parent-ward link
    checkStudentAccess(studentId, principal);
}
```
- **Zero Teacher Exposure**: Under Sindh School Education Department regulatory guidelines, teachers are insulated from tuition collection, payment statuses, and fee disputes to protect academic impartiality.

---

## 5. Frontend Navigation & Persona Verification

The React frontend utilizes `ProtectedRoute` components coupled with dynamic sidebar rendering:

1. **Role-Based Navigation**:
   - `Sidebar.tsx` evaluates `activeRole` against permitted roles for every navigation item.
   - For `ROLE_STUDENT`, navigation is strictly confined to:
     - `Student Dashboard` (`/`)
     - `My Timetable` (`/timetable`)
     - `My Attendance` (`/attendance`)
     - `My Courses & LMS` (`/lms`)
     - `My Report Cards` (`/examinations`)
     - `My Fee Invoices` (`/fees`)
   - Administrative modules (`Academics`, `Admissions`, `Student Directory`, `Reports & Analytics`, `School Settings`) are never rendered in the DOM for student, parent, or teacher personas.

2. **Route Guarding**:
   - If a student manually enters `http://localhost:5173/admissions` or `http://localhost:5173/settings` into the browser URL bar, `ProtectedRoute` intercepts the route and renders the custom `<ForbiddenPage />` explaining the access restriction.

3. **Persona Switcher**:
   - The top header provides an interactive Persona Switcher (`Switch Persona ▾`) allowing administrators and auditors to seamlessly toggle between the 7 personas (`admin`, `principal`, `teacher.ahmed`, `teacher.fatima`, `accountant`, `parent.khan`, `student.ali`) and inspect real-time UI/RBAC behavior.
