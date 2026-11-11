package com.bluebells.idcard;

import com.bluebells.common.ApiResponse;
import com.bluebells.idcard.dto.GenerateIdCardRequest;
import com.bluebells.idcard.dto.ReissueIdCardRequest;
import com.bluebells.idcard.dto.StudentIdCardDto;
import com.bluebells.idcard.dto.VerifyIdCardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/id-cards")
@Tag(name = "Digital Student ID Cards", description = "Student digital identification cards and secure QR verification")
public class StudentIdCardController {

    private final StudentIdCardService cardService;

    public StudentIdCardController(StudentIdCardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT') or hasAuthority('STUDENT_ID_VIEW')")
    @Operation(summary = "Get active digital student ID card")
    public ResponseEntity<ApiResponse<StudentIdCardDto>> getActiveCard(@PathVariable Long studentId) {
        StudentIdCardDto card = cardService.getActiveCardForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(card));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('STUDENT_ID_GENERATE')")
    @Operation(summary = "Generate ID card for an individual student")
    public ResponseEntity<ApiResponse<StudentIdCardDto>> generateCard(@Valid @RequestBody GenerateIdCardRequest req) {
        StudentIdCardDto card = cardService.generateOrGetCard(req);
        return ResponseEntity.ok(ApiResponse.success("ID card generated successfully", card));
    }

    @PostMapping("/generate-bulk")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('STUDENT_ID_GENERATE')")
    @Operation(summary = "Bulk generate ID cards for a section")
    public ResponseEntity<ApiResponse<List<StudentIdCardDto>>> bulkGenerate(
            @RequestParam Long sectionId,
            @RequestParam Long sessionId) {
        List<StudentIdCardDto> cards = cardService.bulkGenerateForSection(sectionId, sessionId);
        return ResponseEntity.ok(ApiResponse.success("Bulk ID cards generated", cards));
    }

    @PostMapping("/{cardId}/reissue")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('STUDENT_ID_GENERATE')")
    @Operation(summary = "Reissue a student ID card with audit reason")
    public ResponseEntity<ApiResponse<StudentIdCardDto>> reissueCard(
            @PathVariable Long cardId,
            @Valid @RequestBody ReissueIdCardRequest req) {
        StudentIdCardDto reissued = cardService.reissueCard(cardId, req);
        return ResponseEntity.ok(ApiResponse.success("ID card reissued successfully", reissued));
    }

    @GetMapping("/verify/{token}")
    @Operation(summary = "Public verification endpoint for Student ID card QR code")
    public ResponseEntity<ApiResponse<VerifyIdCardResponse>> verifyCard(@PathVariable String token) {
        VerifyIdCardResponse res = cardService.verifyCardToken(token);
        return ResponseEntity.ok(ApiResponse.success(res));
    }
}
