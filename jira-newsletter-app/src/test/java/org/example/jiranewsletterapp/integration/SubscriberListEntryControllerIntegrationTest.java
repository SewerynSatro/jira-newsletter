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

@SpringBootTest
@AutoConfigureMockMvc
class SubscriberListEntryControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private SubscriberRepository subscriberRepository;
    @Autowired private SubscriberListRepository listRepository;
    @Autowired private SubscriberListEntryRepository entryRepository;

    private User admin;
    private User user;
    private Subscriber subscriber;
    private SubscriberList list;
    private SubscriberListEntry entry;

    @BeforeEach
    void setUp() {
        entryRepository.deleteAll();
        listRepository.deleteAll();
        userRepository.deleteAll();
        subscriberRepository.deleteAll();

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("pass");
        user.setRole(Role.USER);
        user.setGender(Gender.MALE);
        user.setFirstName("User");
        user.setLastName("Test");
        user = userRepository.save(user);

        admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword("pass");
        admin.setRole(Role.ADMIN);
        admin.setGender(Gender.FEMALE);
        admin.setFirstName("Admin");
        admin.setLastName("Test");
        admin = userRepository.save(admin);

        subscriber = new Subscriber();
        subscriber.setEmail("sub@example.com");
        subscriber.setFirstName("Anna");
        subscriber.setLastName("Nowak");
        subscriber.setGender(Gender.FEMALE);
        subscriber = subscriberRepository.save(subscriber);

        list = new SubscriberList();
        list.setName("Test List");
        list.setOwner(user);
        list = listRepository.save(list);

        entry = new SubscriberListEntry();
        entry.setSubscriber(subscriber);
        entry.setList(list);
        entry.setConfirmed(true);
        entry.setSource("API");
        entry = entryRepository.save(entry);
    }

    private void authenticateAs(User u) {
        var principal = new UserPrincipal(u);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("ADMIN: Should return all entries")
    void testAdminGetAll() throws Exception {
        authenticateAs(admin);
        mockMvc.perform(get("/subscriber-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].subscriber.email").value("sub@example.com"));
    }

    @Test
    @DisplayName("ADMIN: Should get entry by ID")
    void testAdminGetById() throws Exception {
        authenticateAs(admin);
        mockMvc.perform(get("/subscriber-entries/" + entry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriber.email").value("sub@example.com"));
    }

    @Test
    @DisplayName("ADMIN: Should assign subscriber to list")
    void testAdminAssignSubscriber() throws Exception {
        authenticateAs(admin);
        mockMvc.perform(post("/subscriber-entries/assign")
                        .param("listId", list.getId().toString())
                        .param("subscriberId", subscriber.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriber.id").value(subscriber.getId()));
    }

    @Test
    @DisplayName("ADMIN: Should delete entry")
    void testAdminDeleteEntry() throws Exception {
        authenticateAs(admin);
        mockMvc.perform(delete("/subscriber-entries/" + entry.getId()))
                .andExpect(status().isOk());
        assertFalse(entryRepository.findById(entry.getId()).isPresent());
    }

    @Test
    @DisplayName("USER: Should get their own entries")
    void testUserGetMyEntries() throws Exception {
        authenticateAs(user);
        mockMvc.perform(get("/subscriber-entries/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].subscriber.email").value("sub@example.com"));
    }

    @Test
    @DisplayName("USER: Should delete own entry")
    void testUserDeleteOwnEntry() throws Exception {
        authenticateAs(user);
        mockMvc.perform(delete("/subscriber-entries/my/" + entry.getId()))
                .andExpect(status().isOk());
        assertFalse(entryRepository.findById(entry.getId()).isPresent());
    }
}