# Bluebells Support & Help Desk Centre — Privacy & Access Isolation Audit

**Module**: Support & Complaint Centre  
**Backend**: `SupportTicketController.java`, `SupportTicketService.java`, `SupportTicket.java`, `TicketMessage.java`  
**Frontend**: `SupportCentrePage.tsx`  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com>  
**Date**: November 2026  

---

## 1. Overview & Privacy Principles

The Support Centre handles sensitive inquiries from parents, students, and teachers regarding fee discrepancies, academic grading questions, transport routes, and disciplinary concerns.

### Core Privacy Principles
1. **Strict Family Isolation**: Parents must only view support tickets created by themselves or concerning their registered children.
2. **Staff Confidentiality**: Internal administrative deliberations (`isInternalNote: true`) must never be leaked to parents or students.
3. **Accountable Escalation**: Critical unresolved complaints can be escalated directly to Executive Leadership (Principal / Vice Principal).

---

## 2. Privacy Architecture & Security Auditing

### 2.1. Multi-Tenant Parent Isolation & IDOR Defense
In `SupportTicketService.java`:
```java
public SupportTicketDto getTicketDetails(Long ticketId) {
    SupportTicket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    
    User currentUser = getCurrentUser();
    boolean isStaff = currentUser.hasRole("ROLE_SUPER_ADMIN") || currentUser.hasRole("ROLE_SCHOOL_ADMIN")
            || currentUser.hasRole("ROLE_PRINCIPAL") || currentUser.hasRole("ROLE_TEACHER");
    
    if (!isStaff) {
        boolean isCreator = ticket.getCreatedBy().getId().equals(currentUser.getId());
        boolean isLinkedParent = isUserParentOfStudent(currentUser, ticket.getStudent());
        
        if (!isCreator && !isLinkedParent) {
            throw new AccessDeniedException("Access Denied: You do not have permission to view tickets created by other parents or students.");
        }
    }
    ...
}
```

### 2.2. Internal Notes Masking
When compiling messages in a ticket thread:
```java
List<TicketMessageDto> messages = ticket.getMessages().stream()
        .filter(msg -> isStaff || !msg.isInternalNote())
        .map(this::toDto)
        .collect(Collectors.toList());
```
Parents and students never receive internal notes in API payloads, regardless of query parameters.

### 2.3. Leadership Escalation Workflow
Staff can escalate unresolved or high-priority tickets to Executive School Leadership via `POST /api/v1/support/tickets/{id}/escalate?notes=...`. The ticket is tagged with `ESCALATED` status and surfaced in the Principal's priority queue.

---

## 3. Automated Test Verification

All 4 tests in `SupportTicketPrivacyTest.java` pass:
1. `testParentCannotViewOtherParentsTicket`: Confirms HTTP 403 Forbidden on foreign ticket access.
2. `testStaffInternalNotesHiddenFromParent`: Verifies internal staff notes are omitted from parent JSON payloads.
3. `testParentCanCreateAndReadOwnTicket`: Confirms legitimate ticket lifecycle for authorized parent.
4. `testStaffCanResolveTicket`: Confirms staff resolution and status transition.
