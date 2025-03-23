package com.stephen_parinas.budget_tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Extract the username (subject) from the JWT token.
     *
     * @param token The JWT token.
     * @return The username contained in the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     * This method decodes the token and retrieves the claim based on the provided claims resolver function.
     * The resolver function defines how to extract the desired claim from the JWT's claims object.
     *
     * @param token The JWT token containing the claims.
     * @param claimsResolver A function that extracts the desired claims from the Claims object.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim of type T.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     * This method parses the JWT token and retrieves all claims stored in it.
     * It requires the token to be signed using a valid signing key.
     *
     * @param token The JWT token containing the claims.
     * @return The claims to be extracted.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used for JWT creation and validation.
     * This method decodes the base64-encoded secret key and generates a
     * HMAC (Hash-based Message Authentication Code) signing key using the decoded bytes.
     *
     * @return The signing key used for JWT signing and validation
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
