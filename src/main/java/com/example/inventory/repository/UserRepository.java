package com.example.inventory.repository;

import com.example.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     * Used during authentication to load user details.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists.
     * Used for validation during user registration.
     */
    boolean existsByUsername(String username);
}
