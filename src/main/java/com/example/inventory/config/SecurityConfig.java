package com.example.inventory.config;

import com.example.inventory.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - main Spring Security configuration for RBAC.
 *
 * This class configures:
 * - Password encoding (BCrypt)
 * - Authentication provider
 * - HTTP security rules based on roles
 * - JWT filter integration
 * - Stateless session management (for REST API)
 *
 * EnableMethodSecurity allows @PreAuthorize annotations on controllers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Password encoder bean - uses BCrypt for secure password hashing.
     * BCrypt is a salted hash that's computationally expensive to crack.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider - tells Spring Security how to authenticate users.
     * Uses DaoAuthenticationProvider which loads users from our UserDetailsService.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager - used for programmatic authentication (login endpoint).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security filter chain - defines which requests require authentication
     * and what roles are needed for each endpoint.
     *
     * RBAC Rules:
     * - Public endpoints: /api/auth/**, /oauth2/**, /error
     * - ADMIN only: DELETE operations, user management
     * - OPERATOR or ADMIN: POST/PUT on materials (quantity updates)
     * - AUDITOR, OPERATOR, or ADMIN: GET on materials (read access)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless REST API
            // CSRF protection is for browser-based sessions, not needed for JWT
            .csrf(AbstractHttpConfigurer::disable)

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // H2 console for development

                // ADMIN only endpoints
                .requestMatchers(HttpMethod.DELETE, "/api/materials/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // OPERATOR and ADMIN can modify materials
                .requestMatchers(HttpMethod.POST, "/api/materials/**").hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/materials/**").hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers(HttpMethod.PATCH, "/api/materials/**").hasAnyRole("ADMIN", "OPERATOR")

                // AUDITOR, OPERATOR, and ADMIN can view materials
                .requestMatchers(HttpMethod.GET, "/api/materials/**").hasAnyRole("ADMIN", "OPERATOR", "AUDITOR")

                // All authenticated users can access their profile
                .requestMatchers("/api/users/me").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Configure session management to be stateless
            // JWT tokens are sent with each request, no server-side session needed
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configure authentication provider
            .authenticationProvider(authenticationProvider())

            // Add JWT filter before the standard username/password filter
            // This ensures JWT validation happens early in the filter chain
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // Allow H2 console to work with frames (development only)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
