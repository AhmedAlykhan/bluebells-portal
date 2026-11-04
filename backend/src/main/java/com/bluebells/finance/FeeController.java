package com.bluebells.finance;

import com.bluebells.common.ApiResponse;
import com.bluebells.exception.ForbiddenException;
import com.bluebells.finance.dto.*;
import com.bluebells.security.AuthorizationService;
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
@RequestMapping("/api/v1/finance")
@Tag(name = "Fees & Finance", description = "Fee structures, invoicing, payment receipts, and parent financial isolation")
public class FeeController {

    private final FeeService feeService;
    private final AuthorizationService authorizationService;

    public FeeController(FeeService feeService, AuthorizationService authorizationService) {
        this.feeService = feeService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/structures")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('FEES_MANAGE')")
    @Operation(summary = "Define or update a class fee structure item")
    public ResponseEntity<ApiResponse<FeeStructureDto>> createFeeStructure(@Valid @RequestBody CreateFeeStructureRequest request) {
        FeeStructureDto dto = feeService.createFeeStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fee structure configured successfully", dto));
    }

    @GetMapping("/structures")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get fee structures for a session and class")
    public ResponseEntity<ApiResponse<List<FeeStructureDto>>> getFeeStructures(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(required = false) Long classId) {
        List<FeeStructureDto> structures = feeService.getFeeStructures(sessionId, classId);
        return ResponseEntity.ok(ApiResponse.success(structures));
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('FEES_MANAGE')")
    @Operation(summary = "Get all fee invoices with optional status and month filtering")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getAllInvoices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String billingMonth) {
        List<FeeInvoiceDto> invoices = feeService.getAllInvoices(status, billingMonth);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('FEES_MANAGE')")
    @Operation(summary = "Create an individual customized student invoice")
    public ResponseEntity<ApiResponse<FeeInvoiceDto>> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        FeeInvoiceDto invoice = feeService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice created successfully", invoice));
    }

    @PostMapping("/invoices/generate-monthly")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('FEES_MANAGE')")
    @Operation(summary = "Batch generate recurring monthly fee invoices for enrolled students")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> generateMonthlyInvoices(@Valid @RequestBody GenerateMonthlyInvoicesRequest request) {
        List<FeeInvoiceDto> invoices = feeService.generateMonthlyInvoices(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Generated " + invoices.size() + " monthly fee invoices", invoices));
    }

    @GetMapping("/my-invoices")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student's own fee invoices")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getMyInvoices(
            @AuthenticationPrincipal UserPrincipal principal) {
        com.bluebells.students.Student student = authorizationService.getStudentForUser(principal.getId())
                .orElseThrow(() -> new com.bluebells.exception.ResourceNotFoundException("Student profile not found"));
        List<FeeInvoiceDto> invoices = feeService.getInvoicesForStudent(student.getId());
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/invoices/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get invoice details by ID with parent isolation check")
    public ResponseEntity<ApiResponse<FeeInvoiceDto>> getInvoiceById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        FeeInvoiceDto invoice = feeService.getInvoiceById(id);
        authorizationService.checkFeeAccess(invoice.getStudentId(), principal);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    @GetMapping("/students/{studentId}/invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get fee invoice ledger for student with row-level parent isolation")
    public ResponseEntity<ApiResponse<List<FeeInvoiceDto>>> getStudentInvoices(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkFeeAccess(studentId, principal);
        List<FeeInvoiceDto> invoices = feeService.getInvoicesForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('PAYMENTS_RECORD')")
    @Operation(summary = "Get all recorded fee payments for finance office")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getAllPayments() {
        List<PaymentDto> payments = feeService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT') or hasAuthority('PAYMENTS_RECORD')")
    @Operation(summary = "Record fee payment and generate receipt with FIFO invoice allocation")
    public ResponseEntity<ApiResponse<PaymentDto>> recordPayment(
            @Valid @RequestBody RecordPaymentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        PaymentDto payment = feeService.recordPayment(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully", payment));
    }

    @GetMapping("/students/{studentId}/payments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    @Operation(summary = "Get payment receipts for student with row-level parent isolation")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getStudentPayments(
            @PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        authorizationService.checkFeeAccess(studentId, principal);
        List<PaymentDto> payments = feeService.getPaymentsForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
}
