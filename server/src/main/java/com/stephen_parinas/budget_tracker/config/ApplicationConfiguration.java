package com.stephen_parinas.budget_tracker.config;

import com.stephen_parinas.budget_tracker.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Application-wide configuration for authentication and password encoding.
 * Defines beans for {@link UserDetailsService}, authentication provider, and password encoder.
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
     * @return A lambda-based implementation of {@link UserDetailsService}.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
