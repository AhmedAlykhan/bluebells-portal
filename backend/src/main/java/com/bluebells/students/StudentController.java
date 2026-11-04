package com.bluebells.students;

import com.bluebells.common.ApiResponse;
import com.bluebells.common.PageResponse;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.security.AuthorizationService;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.dto.CreateStudentRequest;
import com.bluebells.students.dto.StudentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students", description = "Student management, profiles, and enrollment endpoints")
public class StudentController {

    private final StudentService studentService;
    private final AuthorizationService authorizationService;

    public StudentController(StudentService studentService, AuthorizationService authorizationService) {
        this.studentService = studentService;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get the authenticated student's own profile")
    public ResponseEntity<ApiResponse<StudentDto>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        StudentDto student = studentService.getStudentByUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'ACCOUNTANT') or hasAuthority('STUDENT_MANAGE')")
    @Operation(summary = "Get paginated list of students with optional search query")
    public ResponseEntity<ApiResponse<PageResponse<StudentDto>>> getStudents(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20, sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<StudentDto> page = studentService.getStudents(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get student profile by ID with strict parent/student access controls")
    public ResponseEntity<ApiResponse<StudentDto>> getStudentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        authorizationService.checkStudentAccess(id, principal);

        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('STUDENT_MANAGE')")
    @Operation(summary = "Direct registration and onboarding of a student")
    public ResponseEntity<ApiResponse<StudentDto>> createStudent(
            @Valid @RequestBody CreateStudentRequest request) {
        StudentDto created = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student registered successfully", created));
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('STUDENT_MANAGE')")
    @Operation(summary = "Enroll an existing student into a new academic session and section")
    public ResponseEntity<ApiResponse<StudentDto>> enrollStudent(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        Long sectionId = ((Number) body.get("sectionId")).longValue();
        Long sessionId = body.get("sessionId") != null ? ((Number) body.get("sessionId")).longValue() : null;
        String rollNumber = (String) body.get("rollNumber");

        StudentDto enrolled = studentService.enrollStudent(id, sectionId, sessionId, rollNumber);
        return ResponseEntity.ok(ApiResponse.success("Student enrolled successfully", enrolled));
    }
}
