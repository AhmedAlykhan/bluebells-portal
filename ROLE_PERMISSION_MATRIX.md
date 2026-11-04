# Role-Based & Resource-Level Permission Matrix — Bluebells School LMS

## 1. User Roles

1. **`SUPER_ADMIN`**: Full platform control, technical system configuration, tenant settings, high-level user administration.
2. **`SCHOOL_ADMIN`**: Day-to-day school administrative operations: sessions, classes, subjects, admissions, student records, fee configuration, staff directory.
3. **`PRINCIPAL`**: Academic oversight, approval of examination results, attendance monitoring, institutional reporting, disciplinary workflows.
4. **`TEACHER`**: Assigned classes/sections: daily attendance, course material publication, assignment lifecycle, marks entry for assigned subjects.
5. **`PARENT` (Guardian)**: Explicitly linked children only: view attendance, timetables, homework, published exam report cards, fee invoices, payment receipts.
6. **`STUDENT`**: Self-records only: view personal timetable, assigned lessons, pending assignments, submit homework, inspect published grades and attendance.
7. **`ACCOUNTANT`**: Financial operations: invoice generation, payment processing, fee adjustments, fee concessions, daily collection reports.

---

## 2. Functional Permission Matrix

| Module / Operation | SUPER_ADMIN | SCHOOL_ADMIN | PRINCIPAL | TEACHER | PARENT | STUDENT | ACCOUNTANT |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| **Identity & Security** | | | | | | | |
| Manage System Users & Roles | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Manage Staff Accounts | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Reset User Passwords | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| View Security Audit Logs | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **School & Academics** | | | | | | | |
| Configure School Profile | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Manage Sessions & Terms | ✅ | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ |
| Manage Classes & Sections | ✅ | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ |
| Manage Subjects & Curriculum | ✅ | ✅ | 👁️ | 👁️ | ❌ | ❌ | ❌ |
| Assign Teachers to Classes | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Admissions & Students** | | | | | | | |
| Manage Admission Inquiries | ✅ | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ |
| Approve / Enroll Students | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Edit Student Profiles | ✅ | ✅ | 👁️ | ❌ | ❌ | ❌ | ❌ |
| View Student Directory | ✅ | ✅ | ✅ | 🎯 Assigned | 🎯 Linked | 🎯 Self | 👁️ |
| Link Guardian to Student | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Timetable & Attendance** | | | | | | | |
| Manage Timetable Grid | ✅ | ✅ | ✅ | 👁️ | ❌ | ❌ | ❌ |
| Record Daily Attendance | ✅ | ✅ | ❌ | 🎯 Assigned | ❌ | ❌ | ❌ |
| Request Attendance Correction | ❌ | ❌ | ❌ | 🎯 Assigned | ❌ | ❌ | ❌ |
| Approve Attendance Correction | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| View Attendance History | ✅ | ✅ | ✅ | 🎯 Assigned | 🎯 Linked | 🎯 Self | ❌ |
| **LMS & Assignments** | | | | | | | |
| Create / Edit Courses & Lessons| ✅ | ✅ | 👁️ | 🎯 Assigned | ❌ | ❌ | ❌ |
| Publish Assignments | ✅ | ✅ | 👁️ | 🎯 Assigned | ❌ | ❌ | ❌ |
| Submit Assignment Homework | ❌ | ❌ | ❌ | ❌ | ❌ | 🎯 Self | ❌ |
| Grade Submissions & Feedback | ✅ | ❌ | 👁️ | 🎯 Assigned | ❌ | ❌ | ❌ |
| View Assignment Grades | ✅ | ✅ | ✅ | 🎯 Assigned | 🎯 Linked | 🎯 Self | ❌ |
| **Examinations & Results** | | | | | | | |
| Schedule Examinations | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Enter Subject Marks | ✅ | ❌ | ❌ | 🎯 Assigned | ❌ | ❌ | ❌ |
| Review / Approve Marks | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Publish Term Results | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| View Published Report Cards | ✅ | ✅ | ✅ | 🎯 Assigned | 🎯 Linked | 🎯 Self | ❌ |
| **Fees & Finance** | | | | | | | |
| Configure Fee Structures | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Generate Student Invoices | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Apply Concessions / Discounts | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Record Fee Payments | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Reverse Payment / Adjust Ledger| ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ (Audited)|
| View Invoices & Receipts | ✅ | ✅ | 👁️ | ❌ | 🎯 Linked | 🎯 Self | ✅ |
| Generate Financial Reports | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |

*Legend: ✅ Full Access | 👁️ Read-Only | 🎯 Scoped Ownership / Resource Authorization | ❌ No Access*

---

## 3. Strict Resource-Level Authorization Rules

1. **Guardian Child Isolation**:
   - A parent `P` has access to student records `S` if and only if a row exists in `guardian_student_links` with `guardian_id = P.guardian_id` and `student_id = S.id`.
   - Parents with multiple children can switch active profiles, but any attempt to query, read, or export data for an unlinked student returns `HTTP 403 Forbidden`.
2. **Teacher Class / Subject Isolation**:
   - A teacher `T` can only record attendance or enter examination marks for class section `Sec` and subject `Sub` if `teacher_assignments` contains an active assignment for `(T.id, Sec.id, Sub.id)`.
3. **Student Self-Service Isolation**:
   - A student `S` can only query assignment submissions, published marks, personal attendance, and tuition invoices where `student_id = S.id`.
4. **Draft vs. Published Examination Results**:
   - Students and parents CANNOT view draft, submitted, or under-review marks entries. They can ONLY access results when the examination status is `PUBLISHED`.
5. **No Client-Supplied Trust**:
   - The backend validates caller identity from the verified authenticated principal (JWT/session token) and never trusts query parameters or request body IDs (e.g. `studentId`, `guardianId`, `role`) for authorization.
