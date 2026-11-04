package com.bluebells.lms;

import com.bluebells.common.ApiResponse;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.lms.dto.*;
import com.bluebells.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lms")
@Tag(name = "LMS & Assignments", description = "Courseware, curriculum modules, lessons, multimedia resources, YouTube video integration, digital assignments, multi-photo student submissions, and grading lifecycle")
public class LmsController {

    private final LmsService lmsService;
    private final FileStorageService fileStorageService;
    private final com.bluebells.security.AuthorizationService authorizationService;

    public LmsController(
            LmsService lmsService,
            FileStorageService fileStorageService,
            com.bluebells.security.AuthorizationService authorizationService) {
        this.lmsService = lmsService;
        this.fileStorageService = fileStorageService;
        this.authorizationService = authorizationService;
    }

    // =========================================================================
    // Courses
    // =========================================================================

    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Create a course linked to a subject, section, and teacher")
    public ResponseEntity<ApiResponse<CourseDto>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseDto created = lmsService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", created));
    }

    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "List all published courses")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getAllCourses() {
        List<CourseDto> courses = lmsService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses));
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

    @GetMapping("/courses/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER')")
    @Operation(summary = "List courses instructed by a specific teacher")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getCoursesForTeacher(@PathVariable Long teacherId) {
        List<CourseDto> courses = lmsService.getCoursesForTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/courses/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "List courses for an enrolled student with progress percentage")
    public ResponseEntity<ApiResponse<List<CourseDto>>> getCoursesForStudent(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkStudentAccess(studentId, principal);
        List<CourseDto> courses = lmsService.getCoursesForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    // =========================================================================
    // Modules
    // =========================================================================

    @PostMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Add a curriculum chapter/module to a course")
    public ResponseEntity<ApiResponse<CourseModuleDto>> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateModuleRequest request) {
        CourseModuleDto created = lmsService.createModule(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course module created successfully", created));
    }

    @GetMapping("/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "List all modules in a course")
    public ResponseEntity<ApiResponse<List<CourseModuleDto>>> getModulesForCourse(@PathVariable Long courseId) {
        List<CourseModuleDto> modules = lmsService.getModulesForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    // =========================================================================
    // Lessons & Progress
    // =========================================================================

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
    public ResponseEntity<ApiResponse<List<LessonDto>>> getLessonsForCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (studentId != null) {
            authorizationService.checkStudentAccess(studentId, principal);
            List<LessonDto> lessons = lmsService.getLessonsForCourseWithStudentStatus(courseId, studentId);
            return ResponseEntity.ok(ApiResponse.success(lessons));
        }
        List<LessonDto> lessons = lmsService.getLessonsForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @PostMapping("/lessons/{lessonId}/progress")
    @PreAuthorize("hasAnyRole('STUDENT', 'SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    @Operation(summary = "Mark lesson completion status for a student")
    public ResponseEntity<ApiResponse<LessonProgressDto>> markLessonProgress(
            @PathVariable Long lessonId,
            @Valid @RequestBody MarkLessonProgressRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkStudentAccess(request.getStudentId(), principal);
        LessonProgressDto progress = lmsService.markLessonProgress(lessonId, request.getStudentId(), request.isCompleted());
        return ResponseEntity.ok(ApiResponse.success("Lesson progress updated successfully", progress));
    }

    @GetMapping("/courses/{courseId}/progress/student/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get overall completion metrics for a student in a course")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentProgressForCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkStudentAccess(studentId, principal);
        Map<String, Object> progress = lmsService.getStudentProgressForCourse(courseId, studentId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    // =========================================================================
    // Learning Resources
    // =========================================================================

    @PostMapping("/courses/{courseId}/resources")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Link an existing file or external URL as a course learning resource")
    public ResponseEntity<ApiResponse<LearningResourceDto>> createResource(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateResourceRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        LearningResourceDto resource = lmsService.createResource(courseId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Learning resource added successfully", resource));
    }

    @PostMapping(value = "/courses/{courseId}/resources/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Upload a PDF, DOCX, PPTX, or image document as a learning resource")
    public ResponseEntity<ApiResponse<LearningResourceDto>> uploadResourceFile(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        LearningResourceDto resource = lmsService.uploadResourceFile(courseId, moduleId, lessonId, title, description, file, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource file uploaded successfully", resource));
    }

    @GetMapping("/courses/{courseId}/resources")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get all learning resources for a course")
    public ResponseEntity<ApiResponse<List<LearningResourceDto>>> getResourcesForCourse(@PathVariable Long courseId) {
        List<LearningResourceDto> resources = lmsService.getResourcesForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(resources));
    }

    @GetMapping("/resources/{resourceId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get resource metadata by ID")
    public ResponseEntity<ApiResponse<LearningResourceDto>> getResourceById(@PathVariable Long resourceId) {
        LearningResourceDto resource = lmsService.getResourceById(resourceId);
        return ResponseEntity.ok(ApiResponse.success(resource));
    }

    // =========================================================================
    // YouTube Video Integration
    // =========================================================================

    @PostMapping("/courses/{courseId}/videos")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER') or hasAuthority('LMS_MANAGE')")
    @Operation(summary = "Integrate a YouTube video lecture into a course")
    public ResponseEntity<ApiResponse<YouTubeVideoDto>> addYouTubeVideo(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateYouTubeVideoRequest request) {
        YouTubeVideoDto video = lmsService.addYouTubeVideo(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("YouTube video lecture integrated successfully", video));
    }

    @GetMapping("/courses/{courseId}/videos")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "List all integrated YouTube video lectures for a course")
    public ResponseEntity<ApiResponse<List<YouTubeVideoDto>>> getVideosForCourse(@PathVariable Long courseId) {
        List<YouTubeVideoDto> videos = lmsService.getVideosForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    // =========================================================================
    // Assignments
    // =========================================================================

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
    @Operation(summary = "Get all assignments for a course with optional student submission state")
    public ResponseEntity<ApiResponse<List<AssignmentDto>>> getAssignmentsForCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (studentId != null) {
            authorizationService.checkStudentAccess(studentId, principal);
            List<AssignmentDto> assignments = lmsService.getAssignmentsForCourseWithStudentSubmission(courseId, studentId);
            return ResponseEntity.ok(ApiResponse.success(assignments));
        }
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

    // =========================================================================
    // Submissions & Multi-Photo Uploads
    // =========================================================================

    @PostMapping("/assignments/{assignmentId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    @Operation(summary = "Submit an assignment solution via JSON with optional multi-file links")
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

    @PostMapping(value = "/assignments/{assignmentId}/submit-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    @Operation(summary = "Submit handwritten homework by uploading multiple photos or PDF attachments")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> submitAssignmentWithFiles(
            @PathVariable Long assignmentId,
            @RequestParam("studentId") Long studentId,
            @RequestParam(value = "submissionText", required = false) String submissionText,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkStudentAccess(studentId, principal);
        AssignmentSubmissionDto submission = lmsService.submitAssignmentWithFiles(assignmentId, studentId, submissionText, files, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Multi-page homework submitted successfully", submission));
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
    @Operation(summary = "Grade a student submission with marks, feedback, or return for correction")
    public ResponseEntity<ApiResponse<AssignmentSubmissionDto>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AssignmentSubmissionDto graded = lmsService.gradeSubmission(submissionId, request, principal);
        String actionMsg = "RETURNED_FOR_CORRECTION".equalsIgnoreCase(request.getStatus())
                ? "Assignment returned to student for correction"
                : "Assignment submission graded successfully";
        return ResponseEntity.ok(ApiResponse.success(actionMsg, graded));
    }

    // =========================================================================
    // Authorized File Streaming & Downloads
    // =========================================================================

    @GetMapping("/files/stream")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Securely stream or preview an authorized document or image")
    public ResponseEntity<Resource> streamFile(
            @RequestParam("path") String filePath,
            @RequestParam(value = "download", defaultValue = "false") boolean download) {

        // Remove any leading slash or storage prefix if present
        String sanitizedPath = filePath.startsWith("/storage/uploads/")
                ? filePath.substring("/storage/uploads/".length())
                : (filePath.startsWith("storage/uploads/") ? filePath.substring("storage/uploads/".length()) : filePath);

        Resource resource = fileStorageService.loadFileAsResource(sanitizedPath);

        String mimeType = null;
        try {
            mimeType = URLConnection.guessContentTypeFromName(resource.getFilename());
        } catch (Exception ignored) {
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        String disposition = download
                ? "attachment; filename=\"" + resource.getFilename() + "\""
                : "inline; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(resource);
    }
}
