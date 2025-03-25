package com.stephen_parinas.budget_tracker.response;

/**
 * The response returned upon successful user login.
 * Contains the authentication token and its expiration time.
 */
public class LoginResponse {
    private String token;
    private long expiresIn;

    public LoginResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    // Getters
    public String getToken() { return token; }
    public long getExpiresIn() { return expiresIn; }

    // Setters
    public void setToken(String token) { this.token = token; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
