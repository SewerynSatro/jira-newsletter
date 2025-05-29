package org.example.jiranewsletterapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jiranewsletterapp.entity.Gender;
import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.repository.SubscriberRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
class SubscriberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Subscriber testSubscriber;

    @BeforeEach
    void setUp() {
        subscriberRepository.deleteAll();

        testSubscriber = new Subscriber();
        testSubscriber.setEmail("subscriber@example.com");
        testSubscriber.setFirstName("Anna");
        testSubscriber.setLastName("Kowalska");
        testSubscriber.setGender(Gender.FEMALE);

        testSubscriber = subscriberRepository.save(testSubscriber);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should return all subscribers")
    void testGetAllSubscribers() throws Exception {
        mockMvc.perform(get("/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("subscriber@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should return subscriber by ID")
    void testGetSubscriberById() throws Exception {
        mockMvc.perform(get("/subscribers/" + testSubscriber.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("subscriber@example.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should create new subscriber")
    void testCreateSubscriber() throws Exception {
        Subscriber newSubscriber = new Subscriber();
        newSubscriber.setEmail("newsub@example.com");
        newSubscriber.setFirstName("Jan");
        newSubscriber.setLastName("Nowak");
        newSubscriber.setGender(Gender.MALE);

        mockMvc.perform(post("/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSubscriber)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newsub@example.com"));

        assertTrue(subscriberRepository.findByEmail("newsub@example.com").isPresent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should update subscriber")
    void testUpdateSubscriber() throws Exception {
        Subscriber updated = new Subscriber();
        updated.setEmail("updated@example.com");
        updated.setFirstName("Ela");
        updated.setLastName("Updated");
        updated.setGender(Gender.OTHER);

        mockMvc.perform(put("/subscribers/" + testSubscriber.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        Optional<Subscriber> result = subscriberRepository.findById(testSubscriber.getId());
        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should delete subscriber by ID")
    void testDeleteSubscriberById() throws Exception {
        mockMvc.perform(delete("/subscribers/" + testSubscriber.getId()))
                .andExpect(status().isOk());

        assertFalse(subscriberRepository.findById(testSubscriber.getId()).isPresent());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("Should delete subscriber by email")
    void testDeleteSubscriberByEmail() throws Exception {
        mockMvc.perform(delete("/subscribers/email/" + testSubscriber.getEmail()))
                .andExpect(status().isOk());

        assertFalse(subscriberRepository.findByEmail(testSubscriber.getEmail()).isPresent());
    }
}
