package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inventory Management Service - Main Application Class.
 *
 * A Spring Boot REST application demonstrating:
 * - OAuth2 Authorization Server for token issuance
 * - JWT token-based authentication
 * - RBAC (Role-Based Access Control) with three roles:
 *   - ADMIN: Full access to all endpoints
 *   - OPERATOR: Can modify material quantities
 *   - AUDITOR: Read-only access
 *
 * Security Flow:
 * 1. Client sends username/password to /api/auth/login
 * 2. Server validates credentials and returns JWT token
 * 3. Client includes JWT in Authorization header for subsequent requests
 * 4. JwtAuthenticationFilter validates token and sets up SecurityContext
 * 5. RBAC rules in SecurityConfig enforce role-based access
 *
 * @author Educational Example
 */
@SpringBootApplication
public class InventoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
}
