package com.bluebells.academics;

import com.bluebells.academics.dto.*;
import com.bluebells.common.ApiResponse;
import com.bluebells.staff.dto.CreateTeacherAssignmentRequest;
import com.bluebells.staff.dto.TeacherAssignmentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academics")
@Tag(name = "Academics", description = "Academic setup, sessions, terms, classes, sections, subjects, and teacher assignments")
public class AcademicController {

    private final AcademicService academicService;

    public AcademicController(AcademicService academicService) {
        this.academicService = academicService;
    }

    // Sessions & Terms
    @GetMapping("/sessions")
    @Operation(summary = "Get all academic sessions")
    public ResponseEntity<ApiResponse<List<AcademicSessionDto>>> getSessions() {
        return ResponseEntity.ok(ApiResponse.success(academicService.getSessions()));
    }

    @GetMapping("/sessions/current")
    @Operation(summary = "Get the active academic session")
    public ResponseEntity<ApiResponse<AcademicSessionDto>> getCurrentSession() {
        return ResponseEntity.ok(ApiResponse.success(academicService.getCurrentSession()));
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Create an academic session")
    public ResponseEntity<ApiResponse<AcademicSessionDto>> createSession(@Valid @RequestBody CreateAcademicSessionRequest request) {
        AcademicSessionDto created = academicService.createSession(request);
        return new ResponseEntity<>(ApiResponse.success("Session created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/sessions/{id}/set-current")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Set the active academic session")
    public ResponseEntity<ApiResponse<AcademicSessionDto>> setCurrentSession(@PathVariable Long id) {
        AcademicSessionDto session = academicService.setCurrentSession(id);
        return ResponseEntity.ok(ApiResponse.success("Current session updated", session));
    }

    @GetMapping("/sessions/{id}/terms")
    @Operation(summary = "Get terms belonging to an academic session")
    public ResponseEntity<ApiResponse<List<TermDto>>> getTerms(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(academicService.getTerms(id)));
    }

    @PostMapping("/sessions/{id}/terms")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Add a term to an academic session")
    public ResponseEntity<ApiResponse<TermDto>> createTerm(
            @PathVariable Long id,
            @Valid @RequestBody CreateTermRequest request) {
        TermDto created = academicService.createTerm(id, request);
        return new ResponseEntity<>(ApiResponse.success("Term added successfully", created), HttpStatus.CREATED);
    }

    // Grade Levels, Classes, Sections, Subjects
    @GetMapping("/grade-levels")
    @Operation(summary = "Get all grade levels")
    public ResponseEntity<ApiResponse<List<GradeLevelDto>>> getGradeLevels() {
        return ResponseEntity.ok(ApiResponse.success(academicService.getGradeLevels()));
    }

    @GetMapping("/classes")
    @Operation(summary = "Get all classes with sections and subjects")
    public ResponseEntity<ApiResponse<List<ClassDto>>> getClasses() {
        return ResponseEntity.ok(ApiResponse.success(academicService.getClasses()));
    }

    @PostMapping("/classes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Create a new class")
    public ResponseEntity<ApiResponse<ClassDto>> createClass(@Valid @RequestBody CreateClassRequest request) {
        ClassDto created = academicService.createClass(request);
        return new ResponseEntity<>(ApiResponse.success("Class created successfully", created), HttpStatus.CREATED);
    }

    @PostMapping("/classes/{id}/sections")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Add a section to a class")
    public ResponseEntity<ApiResponse<SectionDto>> createSection(
            @PathVariable Long id,
            @Valid @RequestBody CreateSectionRequest request) {
        SectionDto created = academicService.createSection(id, request);
        return new ResponseEntity<>(ApiResponse.success("Section added successfully", created), HttpStatus.CREATED);
    }

    @GetMapping("/classes/{id}/subjects")
    @Operation(summary = "Get all subjects for a class")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getSubjects(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(academicService.getSubjects(id)));
    }

    @PostMapping("/classes/{id}/subjects")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    @Operation(summary = "Add a subject to a class")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubjectRequest request) {
        SubjectDto created = academicService.createSubject(id, request);
        return new ResponseEntity<>(ApiResponse.success("Subject added successfully", created), HttpStatus.CREATED);
    }

    // Teacher Assignments
    @GetMapping("/assignments")
    @Operation(summary = "Get teacher assignments for a session")
    public ResponseEntity<ApiResponse<List<TeacherAssignmentDto>>> getTeacherAssignments(
            @RequestParam(required = false) Long sessionId) {
        return ResponseEntity.ok(ApiResponse.success(academicService.getTeacherAssignments(sessionId)));
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Assign a teacher to a section and subject")
    public ResponseEntity<ApiResponse<TeacherAssignmentDto>> createTeacherAssignment(
            @Valid @RequestBody CreateTeacherAssignmentRequest request) {
        TeacherAssignmentDto created = academicService.createTeacherAssignment(request);
        return new ResponseEntity<>(ApiResponse.success("Teacher assignment created successfully", created), HttpStatus.CREATED);
    }
}
