package com.bluebells.timetable;

import com.bluebells.common.ApiResponse;
import com.bluebells.timetable.dto.CreateTimetableSlotRequest;
import com.bluebells.timetable.dto.TimetableSlotDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetable")
@Tag(name = "Timetable", description = "Class schedule, period planning, and clash prevention endpoints")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping("/slots")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Create a timetable slot with conflict detection (teacher, section, room)")
    public ResponseEntity<ApiResponse<TimetableSlotDto>> createSlot(@Valid @RequestBody CreateTimetableSlotRequest request) {
        TimetableSlotDto created = timetableService.createSlot(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Timetable slot created successfully", created));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get personalized timetable schedule based on authenticated user role")
    public ResponseEntity<ApiResponse<List<TimetableSlotDto>>> getMyTimetable(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.bluebells.security.UserPrincipal principal) {
        List<TimetableSlotDto> timetable = timetableService.getMyTimetable(principal);
        return ResponseEntity.ok(ApiResponse.success(timetable));
    }

    @GetMapping("/section/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get complete timetable schedule for a section")
    public ResponseEntity<ApiResponse<List<TimetableSlotDto>>> getTimetableForSection(@PathVariable Long sectionId) {
        List<TimetableSlotDto> timetable = timetableService.getTimetableForSection(sectionId);
        return ResponseEntity.ok(ApiResponse.success(timetable));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'TEACHER')")
    @Operation(summary = "Get timetable schedule for a specific teacher")
    public ResponseEntity<ApiResponse<List<TimetableSlotDto>>> getTimetableForTeacher(@PathVariable Long teacherId) {
        List<TimetableSlotDto> timetable = timetableService.getTimetableForTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.success(timetable));
    }

    @DeleteMapping("/slots/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL')")
    @Operation(summary = "Delete a timetable slot")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable Long id) {
        timetableService.deleteSlot(id);
        return ResponseEntity.ok(ApiResponse.success("Timetable slot deleted successfully"));
    }
}
