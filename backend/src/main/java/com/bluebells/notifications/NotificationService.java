package com.bluebells.notifications;

import com.bluebells.exception.ForbiddenException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import com.bluebells.notifications.dto.NotificationDto;
import com.bluebells.notifications.dto.SendNotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailNotificationService emailService;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            EmailNotificationService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @Transactional
    public List<NotificationDto> sendNotification(SendNotificationRequest request) {
        List<Notification> toSave = new ArrayList<>();

        if (request.getRecipientId() != null) {
            User recipient = userRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getRecipientId()));
            Notification n = new Notification(recipient, request.getTitle(), request.getMessage(), request.getCategory());
            toSave.add(n);

            if (recipient.getEmail() != null && !recipient.getEmail().isBlank()) {
                emailService.sendEmail(recipient.getEmail(), request.getTitle(), request.getMessage());
            }
        } else if (request.getTargetRole() != null) {
            String roleName = request.getTargetRole().startsWith("ROLE_") ?
                    request.getTargetRole() : "ROLE_" + request.getTargetRole().toUpperCase();

            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

            List<User> users = userRepository.findAll().stream()
                    .filter(u -> u.getRoles() != null && u.getRoles().contains(role))
                    .collect(Collectors.toList());

            for (User u : users) {
                Notification n = new Notification(u, request.getTitle(), request.getMessage(), request.getCategory());
                toSave.add(n);
                if (u.getEmail() != null && !u.getEmail().isBlank()) {
                    emailService.sendEmail(u.getEmail(), request.getTitle(), request.getMessage());
                }
            }
        }

        List<Notification> saved = notificationRepository.saveAll(toSave);
        return saved.stream().map(NotificationDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ForbiddenException("Access denied: You are not authorized to modify this notification");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationDto.fromEntity(saved);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }
}
