# Bluebells Schooling System — Institutional Analytics & Management Intelligence Report

**Document Reference:** `BSS-ANL-2026-V1`  
**Campus:** Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh  
**Leadership:** Academic Principal Huma Arif | Vice Principal Hana Khan  
**Classification:** Confidential Management Intelligence Specification  

---

## 1. Executive Summary & Core Principles

The Institutional Analytics module of the Bluebells Schooling System transforms raw transactional records into actionable, multi-dimensional school intelligence for executive leadership, academic coordinators, and finance administration.

### Foundational Architectural Principles:
1. **Strict Zero-Fabrication Guarantee**: All counts, percentages, averages, and trend projections are calculated directly from active relational database records (via JPA repositories and native SQL aggregations across `students`, `attendance`, `grades`, `fee_invoices`, `lms_courses`, and `staff`). No artificial stats, mock multipliers, or synthetic accomplishments are introduced.
2. **Deterministic Role-Based Access Control (RBAC)**: Analytics dashboards and exports are restricted exclusively to `ROLE_SUPER_ADMIN`, `ROLE_SCHOOL_ADMIN`, `ROLE_PRINCIPAL`, `ROLE_VICE_PRINCIPAL`, and `ROLE_ACCOUNTANT` (with financial-only scoping for finance officers). Unauthorized roles (`ROLE_STUDENT`, `ROLE_PARENT`) receive strict HTTP `403 Forbidden` responses.
3. **Multi-Dimensional Query Filtering**: Leadership can slice all analytical domains dynamically by:
   - **Academic Session** (`sessionId`)
   - **Date Range** (`startDate` to `endDate`)
   - **Grade Level / Class** (`classId`)
   - **Classroom Section** (`sectionId`)
   - **Subject** (`subjectId`)
   - **Faculty / Teacher** (`teacherId`)

---

## 2. Analytics Domain Catalog & Calculation Formulas

### A. Student Demographics & Strength Analytics
| Metric | Calculation Method / Query Logic | Purpose |
| :--- | :--- | :--- |
| **Total Active Students** | `COUNT(s.id) WHERE s.status = 'ACTIVE'` | Institutional capacity planning & classroom allocation |
| **New Admissions** | `COUNT(s.id) WHERE s.admission_date BETWEEN :start AND :end` | Ingestion pipeline velocity & enrollment growth |
| **Withdrawals** | `COUNT(s.id) WHERE s.status = 'WITHDRAWN'` | Retention monitoring and institutional exit analysis |
| **Class-Wise Strength** | Grouped by `class_entities.name` with total enrolled students | Teacher-student ratio enforcement |
| **Section Distribution** | Grouped by `classes` and `sections` (e.g., Grade 8-A, Grade 8-B) | Balancing section density and laboratory limits |
| **Gender Demographics** | Percentage split (`MALE`, `FEMALE`) across the school | Equity reporting and co-curricular division planning |
| **Session Comparison** | YoY comparison of active enrollments against historical terms | Long-term institutional trajectory assessment |

### B. Attendance & Absenteeism Analytics
- **Overall Attendance Rate (%)**:
  $$\text{Attendance Rate} = \frac{\text{Present Records} + (0.5 \times \text{Late Records})}{\text{Total Attendance Records}} \times 100$$
- **Chronic Absenteeism Watchlist**: Flags any student whose cumulative absences exceed $15\%$ of scheduled school days or who has logged $\ge 3$ consecutive unexcused absences. Provides student name, admission number, class/section, and exact days absent for immediate parental counseling.
- **Monthly Absenteeism Curve**: Tracks seasonal and monthly absence spikes (e.g., examination prep weeks, seasonal flu periods).

### C. Academic Performance & Early Warning Watchlist
- **Institutional Grade Distribution**: Dynamic aggregation across letter grades ($A^+, A, B, C, D, F$) computed over finalized examination schedules and continuous coursework assessments.
- **Subject-Wise Averages**: Class-level mean scores highlighting subjects requiring remedial instruction or faculty peer-support.
- **Academic Support Watchlist (At-Risk Students)**:
  $$\text{Average Score} < 40\% \quad \text{across official examination terms}$$
  Provides faculty with early detection of students requiring mandatory after-school academic clinics before board exams.

### D. Financial & Revenue Ledger Analytics
- **Total Invoiced / Billed ($PKR$)**:
  $$\sum \text{invoice.totalAmount} \quad \text{for generated monthly tuition fee challans}$$
- **Total Collected Revenue ($PKR$)**:
  $$\sum \text{payment.amount} \quad \text{reconciled via Askari Bank / Cashier desk}$$
- **Outstanding Receivable ($PKR$)**:
  $$\text{Total Invoiced} - \text{Total Collected}$$
- **Collection Efficiency Rate (%)**:
  $$\text{Collection Rate} = \frac{\text{Total Collected}}{\text{Total Billed}} \times 100$$
- **Class-Wise Fee Aging & Balance Table**: Identifies which grades carry delinquent tuition balances to target timely SMS payment reminders.

### E. Learning Management System (LMS) Engagement
- **Active Coursework Portfolios**: Count of published courses, syllabus modules, and video lecture units.
- **Assignment Submission Completion Rate (%)**:
  $$\text{Submission Rate} = \frac{\text{Total Submissions Received}}{\text{Total Assigned Student Slots}} \times 100$$
- **Digital Resource Count**: Total downloadable PDFs, presentations, and interactive problem sets hosted in secure local storage.

### F. Staff & Faculty Workload Analytics
- **Department Breakdown**: Distribution of teachers across Sciences, Mathematics, Languages, Islamic Studies, and Humanities.
- **Classroom Workload Index**: Number of distinct weekly instructional hours assigned to each faculty member via timetable scheduling.

---

## 3. Executive Multi-Sheet Excel Workbook Export

Authorized personnel can trigger an on-demand executive workbook download via:
`GET /api/v1/analytics/export/excel?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`

The generated spreadsheet (`Bluebells_Management_Analytics_Report.xlsx`) is structured across six dedicated sheets using Apache POI:
1. **Executive Summary**: Core institutional KPIs, timestamp, and active filter criteria.
2. **Student Demographics**: Class-wise enrollments, section capacities, and gender splits.
3. **Attendance & Absences**: Overall rates, present/late/absent counts, and chronic absentee names.
4. **Academic Performance**: Examination term averages and at-risk student rosters.
5. **Fee Ledger**: Class-wise billed vs collected amounts and outstanding balances.
6. **LMS Engagement**: Course counts, assignment submission statistics, and learning material metrics.

---

## 4. API Specification & Security Guards

| Method | Endpoint | Authorized Roles | Response Schema |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/analytics/overview` | Super Admin, School Admin, Principal, VP, Accountant | `ApiResponse<ManagementAnalyticsDto>` |
| `GET` | `/api/v1/analytics/students` | Super Admin, School Admin, Principal, VP | `ApiResponse<StudentAnalyticsDto>` |
| `GET` | `/api/v1/analytics/attendance` | Super Admin, School Admin, Principal, VP | `ApiResponse<AttendanceAnalyticsDto>` |
| `GET` | `/api/v1/analytics/academics` | Super Admin, School Admin, Principal, VP | `ApiResponse<AcademicAnalyticsDto>` |
| `GET` | `/api/v1/analytics/financial` | Super Admin, School Admin, Principal, VP, Accountant | `ApiResponse<FinancialAnalyticsDto>` |
| `GET` | `/api/v1/analytics/lms` | Super Admin, School Admin, Principal, VP | `ApiResponse<LmsAnalyticsDto>` |
| `GET` | `/api/v1/analytics/staff` | Super Admin, School Admin, Principal, VP | `ApiResponse<StaffAnalyticsDto>` |
| `GET` | `/api/v1/analytics/export/excel`| Super Admin, School Admin, Principal, VP, Accountant | Binary `.xlsx` Spreadsheet Stream |

*Report certified by School Administration, Bluebells Schooling System.*
