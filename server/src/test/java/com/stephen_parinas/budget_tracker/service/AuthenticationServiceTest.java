package com.stephen_parinas.budget_tracker.service;

import com.stephen_parinas.budget_tracker.dto.LoginUserDto;
import com.stephen_parinas.budget_tracker.dto.RegisterUserDto;
import com.stephen_parinas.budget_tracker.dto.VerifyUserDto;
import com.stephen_parinas.budget_tracker.model.User;
import com.stephen_parinas.budget_tracker.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link AuthenticationService} class.
 * Ensures that register, login and verification all behave as expected.
 */
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up a test user
        user = new User("John", "Doe", "john.doe@example.com", "encodedPassword");
        user.setEnabled(false);
        user.setVerificationCode("123456");
        user.setVerificationExpiration(Instant.now().plusSeconds(600));
    }

    @Test
    void testRegister() throws MessagingException {
        // Arrange
        RegisterUserDto request = new RegisterUserDto();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        User registeredUser = authenticationService.register(request);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("john.doe@example.com", registeredUser.getEmail());
        assertFalse(registeredUser.getVerificationCode().isEmpty());
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginUserDto request = new LoginUserDto();
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        // Set user as enabled (not verified)
        user.setEnabled(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        // Act
        User loggedInUser = authenticationService.login(request);

        // Assert
        assertEquals(user, loggedInUser);
    }

    @Test
    void testLogin_FailedNotVerified() {
        // Arrange
        LoginUserDto request = new LoginUserDto();
        request.setEmail("john.doe@example.com");
        request.setPassword("password");

        // Set user as disabled (not verified)
        user.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authenticationService.login(request));
        assertEquals("Please verify your account.", exception.getMessage());
    }

    @Test
    void testVerify_Success() {
        // Arrange
        VerifyUserDto request = new VerifyUserDto();
        request.setEmail("john.doe@example.com");
        request.setVerificationCode("123456");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        authenticationService.verify(request);

        // Assert
        assertTrue(user.isEnabled());
        assertNull(user.getVerificationCode());
        assertNull(user.getVerificationExpiration());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testVerify_ExpiredCode() {
        // Arrange
        user.setVerificationExpiration(Instant.now().minusSeconds(600)); // Set expiration in the past
        VerifyUserDto request = new VerifyUserDto();
        request.setEmail("john.doe@example.com");
        request.setVerificationCode("123456");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authenticationService.verify(request));
        assertEquals("Verification code has expired.", exception.getMessage());
    }

    @Test
    void testVerify_InvalidCode() {
        // Arrange
        VerifyUserDto request = new VerifyUserDto();
        request.setEmail("john.doe@example.com");
        request.setVerificationCode("incorrectCode");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authenticationService.verify(request));
        assertEquals("Invalid verification code.", exception.getMessage());
    }

    @Test
    void testResendVerificationCode_Success() throws MessagingException {
        // Arrange
        String email = "john.doe@example.com";
        user.setEnabled(false); // Ensure the account is not verified
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        authenticationService.resendVerificationCode(email);

        // Assert
        assertNotNull(user.getVerificationCode());
        assertFalse(user.getVerificationCode().isEmpty());
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testResendVerificationCode_AlreadyVerified() {
        // Arrange
        String email = "john.doe@example.com";
        user.setEnabled(true); // Ensure the account is already verified

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authenticationService.resendVerificationCode(email));
        assertEquals("Account is already verified.", exception.getMessage());
    }

    @Test
    void testSendVerificationEmail_Success() throws MessagingException {
        // Arrange
        String subject = "Account Verification";
        String message = "Your verification code is 123456.";
        user.setEmail("john.doe@example.com");

        // Act
        authenticationService.sendVerificationEmail(user);

        // Assert
        verify(emailService, times(1)).sendVerificationEmail(user.getEmail(), subject, message);
    }

    @Test
    void testSendVerificationEmail_Failure() throws MessagingException {
        // Arrange
        doThrow(new MessagingException("Email sending failed")).when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authenticationService.sendVerificationEmail(user));
        assertEquals("jakarta.mail.MessagingException: Email sending failed", exception.getMessage());
    }
}
