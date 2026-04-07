package com.example.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;

/**
 * OAuth2 Configuration - provides JWT decoder for token verification.
 *
 * This educational example uses HMAC-SHA256 for JWT signing (see JwtTokenProvider).
 * This bean shows how RSA-based verification would work in production:
 * - Authorization server signs with private key
 * - Resource servers verify with public key
 * - No shared secrets needed between services
 */
@Configuration
@Slf4j
public class OAuth2AuthorizationServerConfig {

    /**
     * RSA key pair for JWT token signing.
     * Generated at startup for demonstration purposes.
     */
    private static KeyPair keyPair;

    /**
     * JWT decoder for verifying RSA-signed tokens.
     * In production with separate auth/resource servers:
     * - Auth server holds private key for signing
     * - Resource servers use public key for verification
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        if (keyPair == null) {
            generateKeyPair();
        }
        // Use the RSA public key to verify token signatures
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
    }

    /**
     * Generate RSA key pair for token signing.
     * Production considerations:
     * - Store keys in secure key management system (KMS, Vault, etc.)
     * - Rotate keys periodically (e.g., every 90 days)
     * - Use at least 2048-bit key length
     * - Consider using ES256 (ECDSA) for better performance
     */
    private static void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            log.info("Generated RSA key pair for OAuth2 token verification");
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA key pair", ex);
        }
    }
}
