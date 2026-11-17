package com.bluebells.config;

import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Value("${bluebells.initial-admin.username:admin}")
    private String initialAdminUsername;

    @Value("${bluebells.initial-admin.password:}")
    private String initialAdminPassword;

    @Value("${bluebells.initial-admin.email:admin@bluebells.edu.pk}")
    private String initialAdminEmail;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, Environment environment) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername(initialAdminUsername)) {
            log.info("Bootstrapping default administrative user account '{}'...", initialAdminUsername);

            boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
            String adminPassword;
            if (initialAdminPassword != null && !initialAdminPassword.isBlank()) {
                adminPassword = initialAdminPassword;
            } else if (isProd) {
                log.warn("INITIAL_ADMIN_PASSWORD not provided in production environment. Generating a 24-character cryptographically secure password.");
                adminPassword = generateSecurePassword(24);
            } else {
                adminPassword = "Admin@12345";
            }

            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Administrator")));

            Role schoolAdminRole = roleRepository.findByName("ROLE_SCHOOL_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_SCHOOL_ADMIN", "School Administrator")));

            User admin = new User();
            admin.setUsername(initialAdminUsername);
            admin.setEmail(initialAdminEmail != null && !initialAdminEmail.isBlank() ? initialAdminEmail : "admin@bluebells.edu.pk");
            admin.setFullName("Bluebells Platform Administrator");
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setPhoneNumber("+92 316 9681932");
            admin.setStatus("ACTIVE");
            admin.setRoles(Set.of(superAdminRole, schoolAdminRole));

            userRepository.save(admin);
            log.info("Default administrator created successfully: {} (credentials secured)", initialAdminUsername);
        }
    }

    private String generateSecurePassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-_";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
