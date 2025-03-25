package com.stephen_parinas.budget_tracker.controller;

import com.stephen_parinas.budget_tracker.model.User;
import com.stephen_parinas.budget_tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling user-related operations.
 */
@RequestMapping("/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return A {@link ResponseEntity} containing the authenticated {@link User}.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User me = (User) authentication.getPrincipal();
        return ResponseEntity.ok(me);
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link User} entities.
     */
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
