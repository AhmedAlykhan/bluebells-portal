package com.bluebells.security;

import com.bluebells.exception.ForbiddenException;
import com.bluebells.guardians.GuardianService;
import com.bluebells.staff.Staff;
import com.bluebells.staff.StaffRepository;
import com.bluebells.staff.TeacherAssignmentRepository;
import com.bluebells.students.Enrollment;
import com.bluebells.students.EnrollmentRepository;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorizationService {

    private final StudentRepository studentRepository;
    private final GuardianService guardianService;
    private final StaffRepository staffRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AuthorizationService(
            StudentRepository studentRepository,
            GuardianService guardianService,
            StaffRepository staffRepository,
            TeacherAssignmentRepository teacherAssignmentRepository,
            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.guardianService = guardianService;
        this.staffRepository = staffRepository;
        this.teacherAssignmentRepository = teacherAssignmentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public boolean isAdmin(UserPrincipal principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN") ||
                               a.getAuthority().equals("ROLE_SCHOOL_ADMIN") ||
                               a.getAuthority().equals("SUPER_ADMIN") ||
                               a.getAuthority().equals("SCHOOL_ADMIN"));
    }

    public boolean isPrincipal(UserPrincipal principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL") || a.getAuthority().equals("PRINCIPAL"));
    }

    public boolean isTeacher(UserPrincipal principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER") || a.getAuthority().equals("TEACHER"));
    }

    public boolean isAccountant(UserPrincipal principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ACCOUNTANT") || a.getAuthority().equals("ACCOUNTANT"));
    }

    public boolean isParent(UserPrincipal principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT") || a.getAuthority().equals("PARENT"));
    }

    public boolean isStudent(UserPrincipal principal) {
        if (principal == null) return false;
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT") || a.getAuthority().equals("STUDENT"));
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentForUser(Long userId) {
        if (userId == null) return Optional.empty();
        return studentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Staff> getStaffForUser(Long userId) {
        if (userId == null) return Optional.empty();
        return staffRepository.findByUserId(userId);
    }

    /**
     * Checks if the authenticated principal is authorized to view or access a student's academic records
     * (Profile, Attendance, LMS submissions, Results/Report Cards).
     */
    @Transactional(readOnly = true)
    public void checkStudentAccess(Long targetStudentId, UserPrincipal principal) {
        if (principal == null) {
            throw new ForbiddenException("Authentication required to access student records");
        }

        // 1. Admin & Principal have full oversight
        if (isAdmin(principal) || isPrincipal(principal)) {
            return;
        }

        // 2. Student can only access their OWN records
        if (isStudent(principal)) {
            Optional<Student> studentOpt = studentRepository.findByUserId(principal.getId());
            if (studentOpt.isEmpty() || !studentOpt.get().getId().equals(targetStudentId)) {
                throw new ForbiddenException("Access denied: Students may only access their own records (IDOR protection)");
            }
            return;
        }

        // 3. Parent can only access linked children records
        if (isParent(principal)) {
            if (!guardianService.isParentOfStudent(principal.getId(), targetStudentId)) {
                throw new ForbiddenException("Access denied: You are not authorized to view this student's records");
            }
            return;
        }

        // 4. Teacher: verify teacher teaches the student's section or class
        if (isTeacher(principal)) {
            Optional<Staff> staffOpt = staffRepository.findByUserId(principal.getId());
            if (staffOpt.isPresent()) {
                boolean hasAssignments = teacherAssignmentRepository.findAll().stream()
                        .anyMatch(ta -> ta.getTeacher().getId().equals(staffOpt.get().getId()));
                if (!hasAssignments) {
                    return;
                }
                List<Enrollment> enrollments = enrollmentRepository.findByStudentId(targetStudentId);
                boolean teachesSection = enrollments.stream().anyMatch(e ->
                        teacherAssignmentRepository.findBySectionIdAndAcademicSessionId(e.getSection().getId(), e.getAcademicSession().getId())
                                .stream().anyMatch(ta -> ta.getTeacher().getId().equals(staffOpt.get().getId()))
                );
                if (teachesSection) {
                    return;
                }
                throw new ForbiddenException("Access denied: Teachers can only access students enrolled in their assigned classes");
            }
            return;
        }

        throw new ForbiddenException("Access denied: Unauthorized role for student record access");
    }

    /**
     * Checks if the authenticated principal is authorized to view a student's financial records (invoices, payments).
     */
    @Transactional(readOnly = true)
    public void checkFeeAccess(Long targetStudentId, UserPrincipal principal) {
        if (principal == null) {
            throw new ForbiddenException("Authentication required to access financial records");
        }

        // 1. Admin, Principal, Accountant have finance management scope
        if (isAdmin(principal) || isPrincipal(principal) || isAccountant(principal)) {
            return;
        }

        // 2. Parent can view linked children fee invoices
        if (isParent(principal)) {
            if (!guardianService.isParentOfStudent(principal.getId(), targetStudentId)) {
                throw new ForbiddenException("Access denied: You are not authorized to view this student's financial records");
            }
            return;
        }

        // 3. Student can view only own read-only invoices
        if (isStudent(principal)) {
            Optional<Student> studentOpt = studentRepository.findByUserId(principal.getId());
            if (studentOpt.isEmpty() || !studentOpt.get().getId().equals(targetStudentId)) {
                throw new ForbiddenException("Access denied: Students may only view their own fee information");
            }
            return;
        }

        // Teachers, unauthorized staff, or unknown roles: FORBIDDEN
        throw new ForbiddenException("Access denied: Financial records are restricted to finance personnel, guardians, and own student account");
    }

    /**
     * Checks if the authenticated principal can mark attendance for the given section.
     */
    @Transactional(readOnly = true)
    public void checkAttendanceMarking(Long sectionId, UserPrincipal principal) {
        if (principal == null) {
            throw new ForbiddenException("Authentication required to mark attendance");
        }

        if (isAdmin(principal) || isPrincipal(principal)) {
            return;
        }

        if (isTeacher(principal)) {
            Optional<Staff> staffOpt = staffRepository.findByUserId(principal.getId());
            if (staffOpt.isPresent()) {
                boolean hasAssignments = teacherAssignmentRepository.findAll().stream()
                        .anyMatch(ta -> ta.getTeacher().getId().equals(staffOpt.get().getId()));
                if (!hasAssignments) {
                    return;
                }
                boolean isAssigned = teacherAssignmentRepository.findAll().stream()
                        .anyMatch(ta -> ta.getTeacher().getId().equals(staffOpt.get().getId()) &&
                                       ta.getSection().getId().equals(sectionId));
                if (isAssigned) {
                    return;
                }
                throw new ForbiddenException("Access denied: Teachers can only mark attendance for classes assigned to them");
            }
            return;
        }

        throw new ForbiddenException("Access denied: Only authorized teachers and school administrators can mark attendance");
    }
}
