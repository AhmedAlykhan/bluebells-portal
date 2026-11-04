package com.bluebells.identity;

import com.bluebells.common.PageResponse;
import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserSummaryDto> getUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return PageResponse.of(usersPage.map(UserSummaryDto::fromEntity));
    }

    @Transactional
    public UserSummaryDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username '" + request.getUsername() + "' is already registered", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email '" + request.getEmail() + "' is already registered", HttpStatus.CONFLICT);
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            String formattedRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
            Role role = roleRepository.findByName(formattedRole)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", formattedRole));
            roles.add(role);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus("ACTIVE");
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return UserSummaryDto.fromEntity(saved);
    }

    @Transactional
    public UserSummaryDto updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setStatus(request.getStatus());
        User saved = userRepository.save(user);
        return UserSummaryDto.fromEntity(saved);
    }
}
