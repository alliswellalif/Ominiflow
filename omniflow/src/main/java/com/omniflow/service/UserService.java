package com.omniflow.service;

import com.omniflow.model.Role;
import com.omniflow.model.User;
import com.omniflow.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @PostConstruct
    public void seedInitialUsers() {
        if (userRepository.count() == 0) {
            // Seed Admin
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    "admin@omniflow.io",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);

            // Seed Logistics Manager
            User manager = new User(
                    "manager",
                    passwordEncoder.encode("manager123"),
                    "Sarah Jenkins (Logistics Lead)",
                    "manager@omniflow.io",
                    Role.ROLE_MANAGER
            );
            userRepository.save(manager);

            // Seed Operations Staff
            User staff = new User(
                    "staff",
                    passwordEncoder.encode("staff123"),
                    "David Miller (Warehouse Staff)",
                    "staff@omniflow.io",
                    Role.ROLE_STAFF
            );
            userRepository.save(staff);

            auditService.log("INITIAL_SEED", "User", "1", "INFO", "System initialized default users: admin, manager, staff");
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User registerUser(String username, String rawPassword, String fullName, String email, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' is already registered.");
        }

        User user = new User(
                username.trim(),
                passwordEncoder.encode(rawPassword),
                fullName.trim(),
                email.trim().toLowerCase(),
                role != null ? role : Role.ROLE_STAFF
        );
        User saved = userRepository.save(user);
        auditService.log("USER_REGISTER", "User", saved.getId().toString(), "INFO", "New user registered: " + username);
        return saved;
    }

    @Transactional
    public void updateProfile(String username, String fullName, String email, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        user.setFullName(fullName.trim());
        user.setEmail(email.trim().toLowerCase());

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword.trim()));
            auditService.log("PASSWORD_CHANGE", "User", user.getId().toString(), "WARNING", "Password updated for " + username);
        } else {
            auditService.log("PROFILE_UPDATE", "User", user.getId().toString(), "INFO", "Profile details updated for " + username);
        }

        userRepository.save(user);
    }
}

