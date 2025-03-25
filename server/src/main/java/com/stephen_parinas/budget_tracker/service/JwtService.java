package com.stephen_parinas.budget_tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A service class responsible for handling JWT token generation, validation, and extraction.
 */
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param userDetails The user details containing authentication information.
     * @return The generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims for the given user.
     *
     * @param additionalClaims Additional claims to include in the JWT payload.
     * @param userDetails      The user details containing authentication information.
     * @return The generated JWT token.
     */
    public String generateToken(Map<String, Object> additionalClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token against the provided user details.
     *
     * @param token       The JWT token to validate.
     * @param userDetails The user details to verify against.
     * @return {@code true} if the token is valid, otherwise {@code false}.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token The JWT token to check.
     * @return {@code true} if the token has expired, otherwise {@code false}.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

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
     * Extract the expiration date from the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token.
     * This method decodes the token and retrieves the claim based on the provided claims resolver function.
     * The resolver function defines how to extract the desired claim from the JWT's claims object.
     *
     * @param token          The JWT token containing the claims.
     * @param claimsResolver A function that extracts the desired claims from the Claims object.
     * @param <T>            The type of the claim to be extracted.
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
