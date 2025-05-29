package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.example.jiranewsletterapp.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubscriberServiceTest {

    private SubscriberRepository subscriberRepository;
    private UserRepository userRepository;
    private SubscriberService subscriberService;

    @BeforeEach
    void setUp() {
        subscriberRepository = mock(SubscriberRepository.class);
        userRepository = mock(UserRepository.class);
        subscriberService = new SubscriberService(subscriberRepository, userRepository);
    }

    @Test
    @DisplayName("Should return all subscribers")
    void testGetAllSubscribers() {
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setEmail("alice@example.com");

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setEmail("bob@example.com");

        when(subscriberRepository.findAll()).thenReturn(Arrays.asList(subscriber1, subscriber2));

        List<Subscriber> subscribers = subscriberService.getAllSubscribers();

        assertEquals(2, subscribers.size());
        verify(subscriberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return subscriber by ID")
    void testGetSubscriberById() {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail("carol@example.com");

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));

        Subscriber result = subscriberService.getById(1L);

        assertEquals("carol@example.com", result.getEmail());
        verify(subscriberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception if subscriber by ID not found")
    void testGetSubscriberByIdNotFound() {
        when(subscriberRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriberService.getById(99L);
        });

        assertEquals("Subscriber not found with id: 99", exception.getMessage());
        verify(subscriberRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create new subscriber")
    void testCreateSubscriber() {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail("dave@example.com");

        when(subscriberRepository.save(subscriber)).thenReturn(subscriber);

        Subscriber result = subscriberService.create(subscriber);

        assertEquals("dave@example.com", result.getEmail());
        verify(subscriberRepository, times(1)).save(subscriber);
    }

    @Test
    @DisplayName("Should update subscriber")
    void testUpdateSubscriber() {
        Subscriber updated = new Subscriber();
        updated.setEmail("eve@example.com");

        when(subscriberRepository.save(updated)).thenReturn(updated);

        Subscriber result = subscriberService.update(5L, updated);

        assertEquals("eve@example.com", result.getEmail());
        assertEquals(5L, result.getId());
        verify(subscriberRepository, times(1)).save(updated);
    }

    @Test
    @DisplayName("Should delete subscriber by ID")
    void testDeleteSubscriber() {
        subscriberService.delete(3L);

        verify(subscriberRepository, times(1)).deleteById(3L);
    }

    @Test
    @DisplayName("Should delete subscriber by email")
    void testDeleteSubscriberByEmail() {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail("frank@example.com");

        when(subscriberRepository.findByEmail("frank@example.com")).thenReturn(Optional.of(subscriber));

        subscriberService.deleteByEmail("frank@example.com");

        verify(subscriberRepository, times(1)).findByEmail("frank@example.com");
        verify(subscriberRepository, times(1)).delete(subscriber);
    }

    @Test
    @DisplayName("Should throw exception if subscriber by email not found")
    void testDeleteSubscriberByEmailNotFound() {
        when(subscriberRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            subscriberService.deleteByEmail("ghost@example.com");
        });

        assertEquals("Subscriber not found with email: ghost@example.com", exception.getMessage());
        verify(subscriberRepository, times(1)).findByEmail("ghost@example.com");
        verify(subscriberRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return subscribers belonging to current user")
    void testGetMySubscribers() {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setEmail("user@example.com");

        SubscriberListEntry entry = new SubscriberListEntry();
        entry.setSubscriber(subscriber);

        SubscriberList list = new SubscriberList();
        list.setEntries(List.of(entry));

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setSubscriberLists(List.of(list));

        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getUser()).thenReturn(mockUser);

        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        var context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        org.springframework.security.core.context.SecurityContextHolder.setContext(context);

        when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));

        List<Subscriber> result = subscriberService.getMySubscribers();

        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0).getEmail());
    }
}
