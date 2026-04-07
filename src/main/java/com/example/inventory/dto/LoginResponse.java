package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login responses containing JWT tokens.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    /**
     * JWT access token - used for API requests.
     */
    private String accessToken;

    /**
     * Token type - always "Bearer" for OAuth2.
     */
    private String tokenType;

    /**
     * Token expiration time in seconds.
     */
    private long expiresIn;

    /**
     * Username of the authenticated user.
     */
    private String username;
}
