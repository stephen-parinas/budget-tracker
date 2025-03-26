package com.stephen_parinas.budget_tracker.service;

import com.stephen_parinas.budget_tracker.dto.LoginUserDto;
import com.stephen_parinas.budget_tracker.dto.RegisterUserDto;
import com.stephen_parinas.budget_tracker.dto.VerifyUserDto;
import com.stephen_parinas.budget_tracker.model.User;
import com.stephen_parinas.budget_tracker.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

/**
 * A service class responsible for handling authentication-related operations such as
 * user registration, login, and verification.
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    /**
     * Registers a new user by creating an account with an encrypted password and a
     * generated verification code. Sends a verification email to the user.
     *
     * @param request The DTO containing user registration details.
     * @return The registered User entity.
     */
    public User register(RegisterUserDto request) {
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(Instant.now().plusSeconds(10 * 60));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    /**
     * Authenticates a user based on their email and password.
     *
     * @param request The DTO containing user login details.
     * @return The authenticated User entity.
     * @throws RuntimeException If the user is not found or their account is not verified.
     */
    public User login(LoginUserDto request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found."));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your account.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return user;
    }

    /**
     * Verifies a user's account using a verification code.
     *
     * @param request The DTO containing verification details.
     * @throws RuntimeException If the user is not found, the verification code is expired, or the code is incorrect.
     */
    public void verify(VerifyUserDto request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found."));

        if (user.getVerificationExpiration().isBefore(Instant.now())) {
            throw new RuntimeException("Verification code has expired.");
        }

        if (user.getVerificationCode().equals(request.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationExpiration(null);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid verification code.");
        }
    }

    /**
     * Resends a new verification code to the user's email if their account is not yet verified.
     *
     * @param email The email address of the user.
     * @throws RuntimeException If the user is not found or their account is already verified.
     */
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified.");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(Instant.now().plusSeconds(10 * 60));
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    /**
     * Sends a verification email to the user with a generated verification code.
     *
     * @param user The user who will receive the verification email.
     * @throws RuntimeException If the email fails to send.
     */
    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String message = "Your verification code is " + verificationCode + ".";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random 6-digit verification code.
     *
     * @return The generated verification code.
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
