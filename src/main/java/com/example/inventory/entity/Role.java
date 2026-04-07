package com.example.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Role entity - defines user roles for RBAC (Role-Based Access Control).
 *
 * Three roles in this system:
 * - ADMIN: Full access to all endpoints and operations
 * - OPERATOR: Can modify material quantities only
 * - AUDITOR: Read-only access to view materials and quantities
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name - must be unique.
     * Uses ROLE_ prefix as required by Spring Security.
     */
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
