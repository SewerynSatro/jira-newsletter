package org.example.jiranewsletterapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jiranewsletterapp.entity.*;
import org.example.jiranewsletterapp.repository.*;
import org.example.jiranewsletterapp.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class SubscriberListControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriberListRepository listRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User testAdmin;
    private SubscriberList testList;

    @BeforeEach
    void setUp() {
        listRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setPassword("admin123");
        testUser.setRole(Role.USER);
        testUser.setGender(Gender.MALE);
        testUser.setFirstName("User");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);

        testAdmin = new User();
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword("admin123");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setGender(Gender.FEMALE);
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("Admin");
        testAdmin = userRepository.save(testAdmin);

        testList = new SubscriberList();
        testList.setName("Initial List");
        testList.setOwner(testUser);
        testList = listRepository.save(testList);
    }

    private void authenticateAs(User user) {
        var principal = new UserPrincipal(user);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("ADMIN: Should return all subscriber lists")
    void testAdminGetAllLists() throws Exception {
        authenticateAs(testAdmin);

        mockMvc.perform(get("/subscriber-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Initial List"));
    }

    @Test
    @DisplayName("ADMIN: Should get list by ID")
    void testAdminGetById() throws Exception {
        authenticateAs(testAdmin);

        mockMvc.perform(get("/subscriber-lists/" + testList.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Initial List"));
    }

    @Test
    @DisplayName("ADMIN: Should delete list")
    void testAdminDeleteList() throws Exception {
        authenticateAs(testAdmin);

        mockMvc.perform(delete("/subscriber-lists/" + testList.getId()))
                .andExpect(status().isOk());

        assertFalse(listRepository.findById(testList.getId()).isPresent());
    }

    @Test
    @DisplayName("USER: Should return current user's lists")
    void testUserGetMyLists() throws Exception {
        authenticateAs(testUser);

        mockMvc.perform(get("/subscriber-lists/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Initial List"));
    }

    @Test
    @DisplayName("USER: Should return current user's list by ID")
    void testUserGetMyListById() throws Exception {
        authenticateAs(testUser);

        mockMvc.perform(get("/subscriber-lists/my/" + testList.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Initial List"));
    }

    @Test
    @DisplayName("USER: Should create list for current user")
    void testUserCreateMyList() throws Exception {
        authenticateAs(testUser);

        SubscriberList list = new SubscriberList();
        list.setName("User's List");

        mockMvc.perform(post("/subscriber-lists/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User's List"));

        assertTrue(listRepository.findAll().stream()
                .anyMatch(l -> l.getName().equals("User's List")));
    }

    @Test
    @DisplayName("USER: Should update current user's list")
    void testUserUpdateMyList() throws Exception {
        authenticateAs(testUser);

        SubscriberList updated = new SubscriberList();
        updated.setName("User Updated List");

        mockMvc.perform(put("/subscriber-lists/my/" + testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User Updated List"));

        Optional<SubscriberList> updatedList = listRepository.findById(testList.getId());
        assertTrue(updatedList.isPresent());
        assertEquals("User Updated List", updatedList.get().getName());
    }

    @Test
    @DisplayName("USER: Should delete current user's list")
    void testUserDeleteMyList() throws Exception {
        authenticateAs(testUser);

        mockMvc.perform(delete("/subscriber-lists/my/" + testList.getId()))
                .andExpect(status().isOk());

        assertFalse(listRepository.findById(testList.getId()).isPresent());
    }
}
