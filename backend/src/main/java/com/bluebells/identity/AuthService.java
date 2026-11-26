package com.bluebells.identity;

import com.bluebells.exception.ApiException;
import com.bluebells.exception.ResourceNotFoundException;
import com.bluebells.exception.UnauthorizedException;
import com.bluebells.security.JwtTokenProvider;
import com.bluebells.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginAttemptService = loginAttemptService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        // Check if account is temporarily locked
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            log.warn("Login attempt for locked account: {}", user.getUsername());
            throw new ApiException("Account is temporarily locked due to multiple failed login attempts. Please try again later.", HttpStatus.LOCKED);
        }

        // Check account status
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            log.warn("Login attempt for inactive account: {}", user.getUsername());
            throw new UnauthorizedException("Account is not active. Please contact the administrator.");
        }

        // Validate password - SEC-04: Persist failed attempt counter in REQUIRES_NEW transaction before throwing
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAttemptService.recordFailedAttempt(user.getId());
            throw new UnauthorizedException("Invalid username or password");
        }

        // Success: Reset failed attempts & update last login in isolated transaction
        loginAttemptService.resetFailedAttempts(user.getId());

        UserPrincipal principal = UserPrincipal.create(user);
        String token = jwtTokenProvider.generateToken(principal);

        return new AuthResponse(token, 86400, UserSummaryDto.fromEntity(user));
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));
        return UserSummaryDto.fromEntity(user);
    }

    @Transactional
    public void changePassword(UserPrincipal principal, PasswordChangeRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ApiException("Current password does not match", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new ApiException("New password must be different from current password", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("User account is inactive");
        }

        UserPrincipal freshPrincipal = UserPrincipal.create(user);
        String token = jwtTokenProvider.generateToken(freshPrincipal);
        log.info("Token refreshed successfully for user: {}", user.getUsername());
        return new AuthResponse(token, 86400, UserSummaryDto.fromEntity(user));
    }
}
