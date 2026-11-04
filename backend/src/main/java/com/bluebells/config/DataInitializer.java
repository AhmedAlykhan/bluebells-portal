package com.bluebells.config;

import com.bluebells.identity.Role;
import com.bluebells.identity.RoleRepository;
import com.bluebells.identity.User;
import com.bluebells.identity.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            log.info("Bootstrapping default administrative user account 'admin'...");

            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_SUPER_ADMIN", "Super Administrator")));

            Role schoolAdminRole = roleRepository.findByName("ROLE_SCHOOL_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_SCHOOL_ADMIN", "School Administrator")));

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@bluebells.edu.pk");
            admin.setFullName("Bluebells Platform Administrator");
            admin.setPasswordHash(passwordEncoder.encode("Admin@12345"));
            admin.setPhoneNumber("+92 316 9681932");
            admin.setStatus("ACTIVE");
            admin.setRoles(Set.of(superAdminRole, schoolAdminRole));

            userRepository.save(admin);
            log.info("Default administrator created successfully: admin / Admin@12345");
        }
    }
}
