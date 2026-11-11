package com.bluebells.support;

import com.bluebells.exception.ApiException;
import com.bluebells.guardians.Guardian;
import com.bluebells.guardians.GuardianRepository;
import com.bluebells.guardians.GuardianStudentLinkRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.security.UserPrincipal;
import com.bluebells.students.Student;
import com.bluebells.students.StudentRepository;
import com.bluebells.support.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final SupportTicketMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianStudentLinkRepository guardianLinkRepository;

    public SupportTicketService(
            SupportTicketRepository ticketRepository,
            SupportTicketMessageRepository messageRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            GuardianRepository guardianRepository,
            GuardianStudentLinkRepository guardianLinkRepository) {
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.guardianRepository = guardianRepository;
        this.guardianLinkRepository = guardianLinkRepository;
    }

    @Transactional(readOnly = true)
    public List<SupportTicketDto> getTicketsForCurrentUser(String category, String status) {
        User currentUser = getCurrentUser();
        boolean isStaff = isStaffUser(currentUser);

        List<SupportTicket> list;
        if (isStaff) {
            // Staff / Leadership can view all or filtered
            if (status != null && !status.isEmpty()) {
                list = ticketRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());
            } else {
                list = ticketRepository.findAll();
            }
        } else {
            // Parents and Students see ONLY their own tickets
            list = ticketRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        }

        if (category != null && !category.isEmpty()) {
            list = list.stream()
                    .filter(t -> category.equalsIgnoreCase(t.getCategory()))
                    .collect(Collectors.toList());
        }

        return list.stream()
                .map(t -> mapToDto(t, isStaff))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupportTicketDto getTicketById(Long id) {
        User currentUser = getCurrentUser();
        boolean isStaff = isStaffUser(currentUser);

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ticket not found with ID: " + id));

        // Strict Privacy Check: If parent or student, must own the ticket
        if (!isStaff && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You do not have permission to view tickets created by other parents or students.");
        }

        return mapToDto(ticket, isStaff);
    }

    @Transactional
    public SupportTicketDto createTicket(CreateTicketRequest req) {
        User currentUser = getCurrentUser();

        Student student = null;
        if (req.getStudentId() != null) {
            student = studentRepository.findById(req.getStudentId()).orElse(null);
        }

        String dept = req.getAssignedDepartment();
        if (dept == null || dept.isEmpty()) {
            dept = mapCategoryToDepartment(req.getCategory());
        }

        String ticketNumber = "TKT-" + LocalDate.now().getYear() + "-" + String.format("%04d", (ticketRepository.count() + 1));

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setUser(currentUser);
        ticket.setStudent(student);
        ticket.setCategory(req.getCategory().toUpperCase());
        ticket.setSubject(req.getSubject());
        ticket.setDescription(req.getDescription());
        ticket.setPriority(req.getPriority() != null ? req.getPriority().toUpperCase() : "MEDIUM");
        ticket.setAssignedDepartment(dept);
        ticket.setStatus("OPEN");

        SupportTicket saved = ticketRepository.save(ticket);

        // Record initial customer message
        SupportTicketMessage initialMsg = new SupportTicketMessage();
        initialMsg.setTicket(saved);
        initialMsg.setSender(currentUser);
        initialMsg.setSenderName(currentUser.getFullName());
        initialMsg.setSenderRole(currentUser.getRoles().isEmpty() ? "USER" : currentUser.getRoles().iterator().next().getName());
        initialMsg.setMessage(req.getDescription());
        initialMsg.setInternalNote(false);
        messageRepository.save(initialMsg);

        return mapToDto(saved, isStaffUser(currentUser));
    }

    @Transactional
    public SupportTicketMessageDto addMessage(Long ticketId, ReplyTicketRequest req) {
        User currentUser = getCurrentUser();
        boolean isStaff = isStaffUser(currentUser);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ticket not found with ID: " + ticketId));

        if (!isStaff && !ticket.getUser().getId().equals(currentUser.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: You cannot reply to another user's ticket.");
        }

        boolean isInternal = isStaff && req.isInternalNote();

        SupportTicketMessage msg = new SupportTicketMessage();
        msg.setTicket(ticket);
        msg.setSender(currentUser);
        msg.setSenderName(currentUser.getFullName());
        msg.setSenderRole(currentUser.getRoles().isEmpty() ? "USER" : currentUser.getRoles().iterator().next().getName());
        msg.setMessage(req.getMessage());
        msg.setInternalNote(isInternal);

        SupportTicketMessage saved = messageRepository.save(msg);

        // Update ticket status on reply
        if (!isInternal) {
            if (isStaff) {
                ticket.setStatus("AWAITING_RESPONSE");
            } else {
                ticket.setStatus("IN_PROGRESS");
            }
            ticketRepository.save(ticket);
        }

        return mapMessageToDto(saved);
    }

    @Transactional
    public SupportTicketDto updateStatus(Long ticketId, UpdateTicketStatusRequest req) {
        User currentUser = getCurrentUser();
        if (!isStaffUser(currentUser)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: Only authorized staff may update ticket status.");
        }

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ticket not found with ID: " + ticketId));

        ticket.setStatus(req.getStatus().toUpperCase());
        if (req.getResolutionNotes() != null) {
            ticket.setResolutionNotes(req.getResolutionNotes());
        }
        if (req.getAssignedDepartment() != null) {
            ticket.setAssignedDepartment(req.getAssignedDepartment().toUpperCase());
        }
        if (req.getAssignedToId() != null) {
            User assigned = userRepository.findById(req.getAssignedToId()).orElse(null);
            ticket.setAssignedTo(assigned);
        }

        if ("RESOLVED".equalsIgnoreCase(req.getStatus()) || "CLOSED".equalsIgnoreCase(req.getStatus())) {
            ticket.setResolvedAt(Instant.now());
            ticket.setResolvedBy(currentUser);
        }

        SupportTicket saved = ticketRepository.save(ticket);
        return mapToDto(saved, true);
    }

    @Transactional
    public SupportTicketDto escalateTicket(Long ticketId, String escalationNotes) {
        User currentUser = getCurrentUser();
        if (!isStaffUser(currentUser)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied: Only staff can escalate tickets.");
        }

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ticket not found with ID: " + ticketId));

        int newLevel = ticket.getEscalationLevel() + 1;
        ticket.setEscalationLevel(newLevel);
        ticket.setPriority("URGENT");
        ticket.setEscalationNotes(escalationNotes);

        if (newLevel == 1) {
            ticket.setAssignedDepartment("PRINCIPAL_OFFICE");
        }

        SupportTicket saved = ticketRepository.save(ticket);
        return mapToDto(saved, true);
    }

    private String mapCategoryToDepartment(String cat) {
        if (cat == null) return "ADMINISTRATION";
        switch (cat.toUpperCase()) {
            case "ACADEMIC":
            case "TEACHER":
                return "ACADEMICS";
            case "FEE_QUERY":
                return "FINANCE";
            case "TRANSPORT":
                return "TRANSPORT";
            case "TECHNICAL_SUPPORT":
                return "IT_SUPPORT";
            default:
                return "ADMINISTRATION";
        }
    }

    private boolean isStaffUser(User user) {
        return user.getRoles().stream().anyMatch(r ->
                List.of("ROLE_SUPER_ADMIN", "ROLE_SCHOOL_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL", "ROLE_TEACHER", "ROLE_ACCOUNTANT")
                        .contains(r.getName()));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthenticated user");
        }
        String username = auth.getPrincipal() instanceof UserPrincipal ? ((UserPrincipal) auth.getPrincipal()).getUsername() : auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private SupportTicketDto mapToDto(SupportTicket t, boolean isStaffViewer) {
        SupportTicketDto dto = new SupportTicketDto();
        dto.setId(t.getId());
        dto.setTicketNumber(t.getTicketNumber());
        dto.setUserId(t.getUser().getId());
        dto.setUserName(t.getUser().getFullName());
        dto.setUserRole(t.getUser().getRoles().isEmpty() ? "USER" : t.getUser().getRoles().iterator().next().getName());

        if (t.getStudent() != null) {
            dto.setStudentId(t.getStudent().getId());
            dto.setStudentName(t.getStudent().getFirstName() + " " + t.getStudent().getLastName());
            dto.setAdmissionNumber(t.getStudent().getAdmissionNumber());
        }

        dto.setCategory(t.getCategory());
        dto.setSubject(t.getSubject());
        dto.setDescription(t.getDescription());
        dto.setPriority(t.getPriority());
        dto.setAssignedDepartment(t.getAssignedDepartment());
        if (t.getAssignedTo() != null) {
            dto.setAssignedToId(t.getAssignedTo().getId());
            dto.setAssignedToName(t.getAssignedTo().getFullName());
        }
        dto.setStatus(t.getStatus());
        dto.setAttachmentUrl(t.getAttachmentUrl());
        dto.setAttachmentName(t.getAttachmentName());
        dto.setResolutionNotes(t.getResolutionNotes());
        dto.setResolvedAt(t.getResolvedAt());
        if (t.getResolvedBy() != null) {
            dto.setResolvedByName(t.getResolvedBy().getFullName());
        }
        dto.setEscalationLevel(t.getEscalationLevel());
        dto.setEscalationNotes(t.getEscalationNotes());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());

        // Message Thread (Filter out internal notes if viewer is a parent or student!)
        List<SupportTicketMessage> rawMsgs = isStaffViewer
                ? messageRepository.findByTicketIdOrderByCreatedAtAsc(t.getId())
                : messageRepository.findByTicketIdAndInternalNoteFalseOrderByCreatedAtAsc(t.getId());

        dto.setMessages(rawMsgs.stream().map(this::mapMessageToDto).collect(Collectors.toList()));
        return dto;
    }

    private SupportTicketMessageDto mapMessageToDto(SupportTicketMessage m) {
        SupportTicketMessageDto dto = new SupportTicketMessageDto();
        dto.setId(m.getId());
        dto.setTicketId(m.getTicket().getId());
        dto.setSenderId(m.getSender().getId());
        dto.setSenderName(m.getSenderName());
        dto.setSenderRole(m.getSenderRole());
        dto.setMessage(m.getMessage());
        dto.setAttachmentUrl(m.getAttachmentUrl());
        dto.setAttachmentName(m.getAttachmentName());
        dto.setInternalNote(m.isInternalNote());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }
}
