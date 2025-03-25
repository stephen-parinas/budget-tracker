package com.stephen_parinas.budget_tracker.service;

import com.stephen_parinas.budget_tracker.model.User;
import com.stephen_parinas.budget_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A service class responsible for handling user-related operations.
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return A list of all registered users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }
}
