package com.example.inventory.service;

import com.example.inventory.entity.Material;
import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import com.example.inventory.repository.MaterialRepository;
import com.example.inventory.repository.RoleRepository;
import com.example.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * DataInitializer - seeds the database with initial data on application startup.
 *
 * This creates:
 * - Three roles: ROLE_ADMIN, ROLE_OPERATOR, ROLE_AUDITOR
 * - Three users: admin, operator, auditor (with password "password")
 * - Sample inventory materials
 *
 * Implements CommandLineRunner to execute automatically at startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing database with seed data...");

        // Create roles if they don't exist
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role operatorRole = createRoleIfNotExists("ROLE_OPERATOR");
        Role auditorRole = createRoleIfNotExists("ROLE_AUDITOR");

        // Create users with BCrypt encoded passwords
        // Password for all users is "password" for educational purposes
        createUserIfNotExists("admin", "admin@example.com", adminRole);
        createUserIfNotExists("operator", "operator@example.com", operatorRole);
        createUserIfNotExists("auditor", "auditor@example.com", auditorRole);

        // Create sample materials
        createMaterialIfNotExists("MAT-001", "Steel Bolts M10", "High-strength steel bolts, 10mm diameter", 1000, "pieces");
        createMaterialIfNotExists("MAT-002", "Aluminum Sheets 2mm", "2mm thick aluminum sheets, 1x2 meters", 500, "sheets");
        createMaterialIfNotExists("MAT-003", "Copper Wire 5mm", "5mm diameter copper wire spool", 200, "meters");
        createMaterialIfNotExists("MAT-004", "Plastic Granules", "Raw plastic material for molding", 5000, "kg");
        createMaterialIfNotExists("MAT-005", "Lubricating Oil", "Industrial grade lubricating oil", 100, "liters");

        log.info("Database initialization complete!");
        log.info("===========================================");
        log.info("Users created (password: 'password'):");
        log.info("  - admin (ROLE_ADMIN) - full access");
        log.info("  - operator (ROLE_OPERATOR) - can modify quantities");
        log.info("  - auditor (ROLE_AUDITOR) - read-only access");
        log.info("===========================================");
    }

    /**
     * Create a role if it doesn't already exist.
     */
    private Role createRoleIfNotExists(String name) {
        return roleRepository.findByName(name)
            .orElseGet(() -> {
                Role role = new Role(name);
                roleRepository.save(role);
                log.info("Created role: {}", name);
                return role;
            });
    }

    /**
     * Create a user with the given role if it doesn't already exist.
     */
    private void createUserIfNotExists(String username, String email, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, passwordEncoder.encode("password"));
            user.setEmail(email);
            user.setRoles(Set.of(role));
            userRepository.save(user);
            log.info("Created user: {} with role: {}", username, role.getName());
        }
    }

    /**
     * Create a material if it doesn't already exist.
     */
    private void createMaterialIfNotExists(String sku, String name, String description, int quantity, String unit) {
        if (!materialRepository.existsBySku(sku)) {
            Material material = new Material(sku, name, quantity, unit);
            material.setDescription(description);
            materialRepository.save(material);
            log.info("Created material: {} - {}", sku, name);
        }
    }
}
