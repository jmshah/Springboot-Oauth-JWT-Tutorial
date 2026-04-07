package com.example.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * User entity - represents an authenticated user in the system.
 *
 * Each user can have one or more roles which determine their access permissions.
 * Passwords are stored as BCrypt hashes (handled by Spring Security).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for login.
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /**
     * Password stored as BCrypt hash.
     * Never store plain text passwords in production!
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * User's email address.
     */
    @Column(name = "email")
    private String email;

    /**
     * Account enabled flag - can be used to disable accounts without deletion.
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /**
     * Many-to-many relationship with roles.
     * A user can have multiple roles.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
