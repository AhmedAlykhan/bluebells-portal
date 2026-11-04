package com.bluebells.dataimport.service;

import com.bluebells.dataimport.dto.PermissionGrantRequest;
import com.bluebells.dataimport.dto.UserPermissionDto;
import com.bluebells.identity.Permission;
import com.bluebells.identity.PermissionRepository;
import com.bluebells.identity.Role;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataImportPermissionService {

    private static final Logger log = LoggerFactory.getLogger(DataImportPermissionService.class);

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final DataImportAuditService auditService;

    public DataImportPermissionService(UserRepository userRepository,
                                       PermissionRepository permissionRepository,
                                       DataImportAuditService auditService) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<UserPermissionDto> getEligibleUsers() {
        List<User> users = userRepository.findAll();
        List<UserPermissionDto> result = new ArrayList<>();

        for (User u : users) {
            // Include leadership / staff roles (Super Admin, Principal, School Admin, Teacher)
            boolean isEligible = u.getRoles().stream().anyMatch(r ->
                    r.getName().equals("ROLE_SUPER_ADMIN") ||
                    r.getName().equals("ROLE_PRINCIPAL") ||
                    r.getName().equals("ROLE_SCHOOL_ADMIN") ||
                    r.getName().equals("ROLE_TEACHER"));

            if (isEligible) {
                UserPermissionDto dto = new UserPermissionDto();
                dto.setUserId(u.getId());
                dto.setUsername(u.getUsername());
                dto.setFullName(u.getFullName());
                dto.setEmail(u.getEmail());
                dto.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

                // Aggregate both role permissions and direct user permissions that belong to DATA_IMPORT
                Set<String> importPerms = new HashSet<>();
                for (Role r : u.getRoles()) {
                    for (Permission p : r.getPermissions()) {
                        if (p.getCode().startsWith("DATA_IMPORT_")) {
                            importPerms.add(p.getCode());
                        }
                    }
                }
                if (u.getDirectPermissions() != null) {
                    for (Permission p : u.getDirectPermissions()) {
                        if (p.getCode().startsWith("DATA_IMPORT_")) {
                            importPerms.add(p.getCode());
                        }
                    }
                }
                dto.setImportPermissions(new ArrayList<>(importPerms));
                result.add(dto);
            }
        }

        return result;
    }

    @Transactional
    public UserPermissionDto grantPermissions(PermissionGrantRequest request, User grantedBy, String clientIp, String userAgent) {
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        List<Permission> importPerms = permissionRepository.findByModule("DATA_IMPORT");
        Map<String, Permission> permMap = importPerms.stream()
                .collect(Collectors.toMap(Permission::getCode, p -> p));

        for (String code : request.getPermissionCodes()) {
            Permission perm = permMap.get(code);
            if (perm != null) {
                targetUser.getDirectPermissions().add(perm);
            }
        }

        User saved = userRepository.save(targetUser);

        auditService.logAudit(null, "PERMISSIONS_GRANTED", "SUCCESS",
                String.format("Granted permissions [%s] to user %s (ID: %d) by %s",
                        String.join(", ", request.getPermissionCodes()),
                        targetUser.getUsername(), targetUser.getId(),
                        grantedBy != null ? grantedBy.getUsername() : "system"),
                clientIp, userAgent);

        return mapToDto(saved);
    }

    @Transactional
    public UserPermissionDto revokePermissions(PermissionGrantRequest request, User revokedBy, String clientIp, String userAgent) {
        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        Set<String> toRevoke = new HashSet<>(request.getPermissionCodes());
        targetUser.getDirectPermissions().removeIf(p -> toRevoke.contains(p.getCode()));

        User saved = userRepository.save(targetUser);

        auditService.logAudit(null, "PERMISSIONS_REVOKED", "SUCCESS",
                String.format("Revoked permissions [%s] from user %s (ID: %d) by %s",
                        String.join(", ", request.getPermissionCodes()),
                        targetUser.getUsername(), targetUser.getId(),
                        revokedBy != null ? revokedBy.getUsername() : "system"),
                clientIp, userAgent);

        return mapToDto(saved);
    }

    private UserPermissionDto mapToDto(User u) {
        UserPermissionDto dto = new UserPermissionDto();
        dto.setUserId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

        Set<String> importPerms = new HashSet<>();
        for (Role r : u.getRoles()) {
            for (Permission p : r.getPermissions()) {
                if (p.getCode().startsWith("DATA_IMPORT_")) {
                    importPerms.add(p.getCode());
                }
            }
        }
        if (u.getDirectPermissions() != null) {
            for (Permission p : u.getDirectPermissions()) {
                if (p.getCode().startsWith("DATA_IMPORT_")) {
                    importPerms.add(p.getCode());
                }
            }
        }
        dto.setImportPermissions(new ArrayList<>(importPerms));
        return dto;
    }
}
