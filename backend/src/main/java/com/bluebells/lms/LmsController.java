package com.bluebells.lms;

import com.bluebells.common.ApiResponse;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.guardians.GuardianService;
import com.bluebells.lms.dto.*;
import com.bluebells.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lms")
@Tag(name = "LMS & Assignments", description = "Courseware, lessons, digital assignments, student submissions, and grading lifecycle")
public class LmsController {

    private final LmsService lmsService;
    private final com.bluebells.security.AuthorizationService authorizationService;

    public LmsController(LmsService lmsService, com.bluebells.security.AuthorizationService authorizationService) {
        this.lmsService = lmsService;
        this.authorizationService = authorizationService;
    }

    // --- Courses ---

    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Create a course linked to a subject, section, and teacher")
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseDto created = lmsService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", created));
    }

    @GetMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get course details by ID")
    public ResponseEntity<ApiResponse<CourseDto>> getCourseById(@PathVariable Long id) {
        CourseDto course = lmsService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping("/courses/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "List all published courses for a section")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getCoursesForSection(@PathVariable Long sectionId) {
        List<CourseDto> courses = lmsService.getCoursesForSection(sectionId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    // --- Lessons ---

    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Add a lesson to a course")
    public ResponseEntity<ApiResponse<LessonDto>> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateLessonRequest request) {
        LessonDto created = lmsService.createLesson(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson created successfully", created));
    }

    @GetMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get all lessons for a course in sequence order")
    public ResponseEntity<ApiResponse<List<LessonDto>>> getLessonsForCourse(@PathVariable Long courseId) {
        List<LessonDto> lessons = lmsService.getLessonsForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    // --- Assignments ---

    @PostMapping("/courses/{courseId}/assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Publish a homework/assignment for a course")
    public ResponseEntity<ApiResponse<AssignmentDto>> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAssignmentRequest request) {
        AssignmentDto created = lmsService.createAssignment(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assignment published successfully", created));
    }

    @GetMapping("/courses/{courseId}/assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get all assignments for a course")
    public ResponseEntity<ApiResponse<List<AssignmentDto>>> getAssignmentsForCourse(@PathVariable Long courseId) {
        List<AssignmentDto> assignments = lmsService.getAssignmentsForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @GetMapping("/assignments/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get assignment details and instructions by ID")
    public ResponseEntity<ApiResponse<AssignmentDto>> getAssignmentById(@PathVariable Long id) {
        AssignmentDto assignment = lmsService.getAssignmentById(id);
        return ResponseEntity.ok(ApiResponse.success(assignment));
    }

    // --- Submissions & Grading ---

    @PostMapping("/assignments/{assignmentId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    @Operation(summary = "Submit an assignment solution")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> submitAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody SubmitAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (authorizationService.isStudent(principal)) {
            com.bluebells.students.Student student = authorizationService.getStudentForUser(principal.getId())
                    .orElseThrow(() -> new com.bluebells.exception.ResourceNotFoundException("Student profile not found"));
            if (!student.getId().equals(request.getStudentId())) {
                throw new ForbiddenException("Access denied: Students can only submit assignments for their own account");
            }
        }
        AssignmentSubmissionDto submission = lmsService.submitAssignment(assignmentId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assignment submitted successfully", submission));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER')")
    @Operation(summary = "Get all student submissions for an assignment")
    public ResponseEntity<ApiResponse<List<AssignmentSubmissionDto>>> getSubmissionsForAssignment(
            @PathVariable Long assignmentId) {
        List<AssignmentSubmissionDto> submissions = lmsService.getSubmissionsForAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping("/assignments/{assignmentId}/submissions/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get submission details for a specific student with parent isolation guard")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> getStudentSubmission(
            @PathVariable Long assignmentId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {

        authorizationService.checkStudentAccess(studentId, principal);

        AssignmentSubmissionDto submission = lmsService.getStudentSubmission(assignmentId, studentId);
        return ResponseEntity.ok(ApiResponse.success(submission));
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Grade a student submission with marks and feedback")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AssignmentSubmissionDto graded = lmsService.gradeSubmission(submissionId, request, principal);
        return ResponseEntity.ok(ApiResponse.success("Submission graded successfully", graded));
    }
}
