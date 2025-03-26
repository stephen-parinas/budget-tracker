package com.stephen_parinas.budget_tracker.service;

import com.stephen_parinas.budget_tracker.model.User;
import com.stephen_parinas.budget_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link UserService} class.
 * Ensures that user functionality behaves as expected.
 */
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange: Mock the userRepository to return a list of users
        User user1 = new User("John", "Doe", "john.doe@example.com", "password123");
        User user2 = new User("Mark", "Lee", "mark.lee@example.com", "Password123");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // Act: Call the method under test
        List<User> result = userService.getAllUsers();

        // Assert: Verify the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("john.doe@example.com", result.get(0).getEmail());

        assertEquals("Mark", result.get(1).getFirstName());
        assertEquals("Lee", result.get(1).getLastName());
        assertEquals("mark.lee@example.com", result.get(1).getEmail());
    }

    @Test
    void testGetAllUsers_EmptyList() {
        // Arrange: Mock the userRepository to return an empty list
        when(userRepository.findAll()).thenReturn(List.of());

        // Act: Call the method under test
        List<User> result = userService.getAllUsers();

        // Assert: Verify the result is an empty list
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
