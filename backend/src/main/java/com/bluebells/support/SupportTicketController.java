package com.bluebells.support;

import com.bluebells.common.ApiResponse;
import com.bluebells.support.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support")
@Tag(name = "School Support & Complaint Centre", description = "Ticket-based student, parent, and faculty inquiry resolution centre")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT', 'ACCOUNTANT') or hasAuthority('SUPPORT_TICKETS_VIEW')")
    @Operation(summary = "Get support tickets (Parents see only their own, staff see all/departmental)")
    public ResponseEntity<ApiResponse<List<SupportTicketDto>>> getTickets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        List<SupportTicketDto> tickets = ticketService.getTicketsForCurrentUser(category, status);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/tickets/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT', 'ACCOUNTANT') or hasAuthority('SUPPORT_TICKETS_VIEW')")
    @Operation(summary = "Get ticket details and conversation thread (Enforcing strict parent privacy)")
    public ResponseEntity<ApiResponse<SupportTicketDto>> getTicketById(@PathVariable Long id) {
        SupportTicketDto ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    @PostMapping("/tickets")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT', 'ACCOUNTANT') or hasAuthority('SUPPORT_TICKETS_VIEW')")
    @Operation(summary = "Open a new support or complaint ticket")
    public ResponseEntity<ApiResponse<SupportTicketDto>> createTicket(@Valid @RequestBody CreateTicketRequest req) {
        SupportTicketDto ticket = ticketService.createTicket(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket submitted successfully", ticket));
    }

    @PostMapping("/tickets/{id}/messages")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL', 'TEACHER', 'PARENT', 'STUDENT', 'ACCOUNTANT') or hasAuthority('SUPPORT_TICKETS_VIEW')")
    @Operation(summary = "Add a message or staff internal note to a ticket thread")
    public ResponseEntity<ApiResponse<SupportTicketMessageDto>> addMessage(
            @PathVariable Long id,
            @Valid @RequestBody ReplyTicketRequest req) {
        SupportTicketMessageDto msg = ticketService.addMessage(id, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent", msg));
    }

    @PatchMapping("/tickets/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('SUPPORT_TICKETS_MANAGE')")
    @Operation(summary = "Update ticket status, department assignment, or resolution notes")
    public ResponseEntity<ApiResponse<SupportTicketDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest req) {
        SupportTicketDto updated = ticketService.updateStatus(id, req);
        return ResponseEntity.ok(ApiResponse.success("Ticket updated", updated));
    }

    @PostMapping("/tickets/{id}/escalate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'PRINCIPAL', 'VICE_PRINCIPAL') or hasAuthority('SUPPORT_TICKETS_MANAGE')")
    @Operation(summary = "Escalate ticket to higher administrative authority")
    public ResponseEntity<ApiResponse<SupportTicketDto>> escalateTicket(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        SupportTicketDto escalated = ticketService.escalateTicket(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Ticket escalated", escalated));
    }
}
