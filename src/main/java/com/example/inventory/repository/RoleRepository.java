package com.example.inventory.repository;

import com.example.inventory.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a role by its name.
     * Used to look up roles like ROLE_ADMIN, ROLE_OPERATOR, ROLE_AUDITOR.
     */
    Optional<Role> findByName(String name);
}
