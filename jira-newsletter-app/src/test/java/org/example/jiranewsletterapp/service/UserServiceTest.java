package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user by ID")
    void testGetUserById() {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty if user by ID not found")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create new user")
    void testCreateUser() {
        User user = new User();
        user.setEmail("newuser@example.com");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("newuser@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        User updated = new User();
        updated.setEmail("updated@example.com");

        when(userRepository.save(any(User.class))).thenReturn(updated);

        User result = userService.updateUser(1L, updated);

        assertEquals("updated@example.com", result.getEmail());
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(updated);
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
