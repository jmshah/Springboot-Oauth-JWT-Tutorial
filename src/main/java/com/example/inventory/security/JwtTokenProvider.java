package com.example.inventory.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JwtTokenProvider - creates and validates JWT tokens for authentication.
 *
 * JWT (JSON Web Token) is a compact, URL-safe means of representing
 * claims to be transferred between two parties. In our OAuth2 flow,
 * the authorization server issues JWTs as access tokens.
 *
 * Token structure:
 * - Header: Algorithm and token type
 * - Payload: Claims (subject, roles, expiration, etc.)
 * - Signature: Verifies the token hasn't been tampered with
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /**
     * Secret key for signing JWTs.
     * In production, use a longer, randomly generated key stored securely.
     */
    @Value("${app.jwt.secret:mySecretKeyForJwtTokenGenerationWhichShouldBeLongEnough}")
    private String jwtSecret;

    /**
     * JWT token validity duration in milliseconds.
     * Default: 1 hour (3600000 ms)
     */
    @Value("${app.jwt.expiration:3600000}")
    private long jwtExpiration;

    private SecretKey secretKey;

    /**
     * Initialize the secret key after bean construction.
     * Uses HMAC-SHA algorithm for signing.
     */
    @PostConstruct
    public void init() {
        // Ensure the secret is long enough for HS256 (at least 32 bytes recommended)
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        log.info("JwtTokenProvider initialized with HS256 algorithm");
    }

    /**
     * Generate a JWT token from the authentication object.
     *
     * @param authentication Spring Security authentication containing user details
     * @return Signed JWT token string
     */
    public String generateToken(Authentication authentication) {
        // Get the username (subject) from authentication
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        // Extract roles from authorities and format as comma-separated string
        String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        log.info("Generating JWT token for user: {} with roles: {}", username, roles);

        // Build the JWT token with claims
        return Jwts.builder()
            // Set the subject (username)
            .subject(username)
            // Set token issuance time
            .issuedAt(now)
            // Set token expiration time
            .expiration(expiryDate)
            // Add custom claim for user roles
            .claim("roles", roles)
            // Sign the token with our secret key using HS256 algorithm
            .signWith(secretKey)
            // Compact the token into a URL-safe string
            .compact();
    }

    /**
     * Extract the username from a JWT token.
     *
     * @param token The JWT token string
     * @return The username (subject) from the token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    /**
     * Validate a JWT token.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Get the expiration time from a token.
     * Useful for displaying remaining token validity to users.
     */
    public Date getExpirationFromToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();
    }
}
