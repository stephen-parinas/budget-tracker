package com.stephen_parinas.budget_tracker.config;

import com.stephen_parinas.budget_tracker.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Application-wide configuration for authentication and password encoding.
 * <p>Defines beans for user authentication, password encoding, and authentication management.</p>
 */
@Configuration
public class ApplicationConfiguration {
    private final UserRepository userRepository;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Defines a {@link UserDetailsService} bean for retrieving user details from the database.
     *
     * @return An implementation of {@link UserDetailsService} that retrieves users by email.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Provides a password encoder for securely hashing passwords.
     *
     * @return A {@link BCryptPasswordEncoder} instance for encoding and verifying passwords.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides an authentication manager that handles authentication requests.
     *
     * @param config The authentication configuration.
     * @return An {@link AuthenticationManager} instance.
     * @throws Exception If an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures and provides an authentication provider that handles user authentication.
     * <p>Uses {@link DaoAuthenticationProvider} with a {@link UserDetailsService}
     * and a {@link BCryptPasswordEncoder} to authenticate users.</p>
     *
     * @return An {@link AuthenticationProvider} for authentication handling.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
