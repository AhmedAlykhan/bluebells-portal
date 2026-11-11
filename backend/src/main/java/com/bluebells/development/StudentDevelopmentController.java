package com.bluebells.development;

import com.bluebells.common.ApiResponse;
import com.bluebells.development.dto.CreateDevelopmentRecordRequest;
import com.bluebells.development.dto.StudentDevelopmentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/development")
@Tag(name = "Student Development Centre", description = "Co-curricular, sports, competitions, awards and milestones")
public class StudentDevelopmentController {

    private final StudentDevelopmentService developmentService;

    public StudentDevelopmentController(StudentDevelopmentService developmentService) {
        this.developmentService = developmentService;
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT') or hasAuthority('STUDENT_DEVELOPMENT_VIEW')")
    @Operation(summary = "Get verified development records for a student")
    public ResponseEntity<ApiResponse<List<StudentDevelopmentDto>>> getRecordsForStudent(@PathVariable Long studentId) {
        List<StudentDevelopmentDto> records = developmentService.getRecordsForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PostMapping("/records")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER') or hasAuthority('STUDENT_DEVELOPMENT_CREATE')")
    @Operation(summary = "Record new achievement, sports award, or milestone")
    public ResponseEntity<ApiResponse<StudentDevelopmentDto>> createRecord(@Valid @RequestBody CreateDevelopmentRecordRequest req) {
        StudentDevelopmentDto created = developmentService.createRecord(req);
        return ResponseEntity.ok(ApiResponse.success("Achievement recorded successfully", created));
    }

    @PostMapping("/records/{id}/upload-evidence")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER') or hasAuthority('STUDENT_DEVELOPMENT_CREATE')")
    @Operation(summary = "Upload certificate or photographic proof for development record")
    public ResponseEntity<ApiResponse<StudentDevelopmentDto>> uploadEvidence(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        StudentDevelopmentDto updated = developmentService.uploadEvidence(id, file);
        return ResponseEntity.ok(ApiResponse.success("Evidence uploaded successfully", updated));
    }

    @DeleteMapping("/records/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL') or hasAuthority('STUDENT_DEVELOPMENT_EDIT')")
    @Operation(summary = "Delete an achievement record")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        developmentService.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.success("Record deleted", null));
    }
}
