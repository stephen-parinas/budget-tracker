package com.stephen_parinas.budget_tracker.controller;

import com.stephen_parinas.budget_tracker.dto.LoginUserDto;
import com.stephen_parinas.budget_tracker.dto.RegisterUserDto;
import com.stephen_parinas.budget_tracker.dto.VerifyUserDto;
import com.stephen_parinas.budget_tracker.model.User;
import com.stephen_parinas.budget_tracker.response.LoginResponse;
import com.stephen_parinas.budget_tracker.service.AuthenticationService;
import com.stephen_parinas.budget_tracker.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related operations.
 */
@RequestMapping("/v1/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /**
     * Register a new user.
     *
     * @param request The registration details.
     * @return The registered user entity.
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto request) {
        User registeredUser = authenticationService.register(request);
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param request The login details.
     * @return A response containing the authentication token and expiration time.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto request) {
        User authenticatedUser = authenticationService.login(request);
        String token = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(token, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Verify a user's account using a verification code.
     *
     * @param request The verification details.
     * @return A response indicating whether the verification was successful or failed.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyUserDto request) {
        try {
            authenticationService.verify(request);
            return ResponseEntity.ok("Account verified successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resend the verification code to the user's email.
     *
     * @param email The email address of the user requesting a new verification code.
     * @return A response indicating whether the verification code was successfully sent.
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
