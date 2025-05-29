package org.example.jiranewsletterapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.entity.Role;
import org.example.jiranewsletterapp.entity.Gender;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("admin@example.com");
        testUser.setPassword("password123");
        testUser.setRole(Role.ADMIN);
        testUser.setGender(Gender.MALE);
        testUser.setFirstName("Admin");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should return all users")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should return user by ID")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should create new user")
    void testCreateUser() throws Exception {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("pass");
        newUser.setRole(Role.USER);
        newUser.setGender(Gender.FEMALE);
        newUser.setFirstName("New");
        newUser.setLastName("User");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));

        assertTrue(userRepository.findByEmail("new@example.com").isPresent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should update user")
    void testUpdateUser() throws Exception {
        User update = new User();
        update.setEmail("updated@example.com");
        update.setPassword("newpass");
        update.setRole(Role.ADMIN);
        update.setGender(Gender.OTHER);
        update.setFirstName("Updated");
        update.setLastName("Person");

        mockMvc.perform(put("/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        Optional<User> updatedUser = userRepository.findById(testUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("updated@example.com", updatedUser.get().getEmail());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should delete user")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + testUser.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.findById(testUser.getId()).isPresent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should return 404 if user not found")
    void testUserNotFound() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should return 404 when updating user that does not exist")
    void testUpdateUserNotFound() throws Exception {
        User update = new User();
        update.setEmail("ghost@example.com");
        update.setPassword("hiddenpass");
        update.setRole(Role.USER);
        update.setGender(Gender.OTHER);
        update.setFirstName("Ghost");
        update.setLastName("User");

        mockMvc.perform(put("/users/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }
}
