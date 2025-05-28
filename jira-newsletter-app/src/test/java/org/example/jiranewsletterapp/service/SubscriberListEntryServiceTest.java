package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.*;
import org.example.jiranewsletterapp.repository.*;
import org.example.jiranewsletterapp.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubscriberListEntryServiceTest {

    private SubscriberListEntryRepository entryRepository;
    private UserRepository userRepository;
    private SubscriberListRepository listRepository;
    private SubscriberRepository subscriberRepository;
    private SubscriberListEntryService entryService;
    private User testUser;

    @BeforeEach
    void setUp() {
        entryRepository = mock(SubscriberListEntryRepository.class);
        userRepository = mock(UserRepository.class);
        listRepository = mock(SubscriberListRepository.class);
        subscriberRepository = mock(SubscriberRepository.class);

        entryService = new SubscriberListEntryService(entryRepository, userRepository, listRepository, subscriberRepository);

        testUser = new User();
        testUser.setId(1L);
        mockSecurityContextWith(testUser);
    }

    private void mockSecurityContextWith(User user) {
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getUser()).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("Should return all subscriber list entries")
    void testGetAllEntries() {
        SubscriberListEntry entry1 = new SubscriberListEntry();
        SubscriberListEntry entry2 = new SubscriberListEntry();

        when(entryRepository.findAll()).thenReturn(Arrays.asList(entry1, entry2));

        List<SubscriberListEntry> entries = entryService.getAllEntries();

        assertEquals(2, entries.size());
        verify(entryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return entry by ID")
    void testGetEntryById() {
        SubscriberListEntry entry = new SubscriberListEntry();
        when(entryRepository.findById(10L)).thenReturn(Optional.of(entry));

        SubscriberListEntry result = entryService.getById(10L);

        assertNotNull(result);
        verify(entryRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Should throw exception when entry not found by ID")
    void testGetEntryByIdNotFound() {
        when(entryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entryService.getById(99L);
        });

        assertEquals("Entry not found with id: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Should create new subscriber list entry")
    void testCreateEntry() {
        SubscriberListEntry entry = new SubscriberListEntry();
        when(entryRepository.save(entry)).thenReturn(entry);

        SubscriberListEntry result = entryService.createEntry(entry);

        assertNotNull(result);
        verify(entryRepository, times(1)).save(entry);
    }

    @Test
    @DisplayName("Should update subscriber list entry")
    void testUpdateEntry() {
        SubscriberListEntry updated = new SubscriberListEntry();
        when(entryRepository.save(updated)).thenReturn(updated);

        SubscriberListEntry result = entryService.updateEntry(5L, updated);

        assertEquals(5L, result.getId());
        verify(entryRepository, times(1)).save(updated);
    }

    @Test
    @DisplayName("Should delete subscriber list entry by ID")
    void testDeleteEntry() {
        entryService.deleteEntry(8L);

        verify(entryRepository, times(1)).deleteById(8L);
    }

    @Test
    @DisplayName("Should assign subscriber to list as admin")
    void testAssignSubscriberToListAsAdmin() {
        SubscriberList list = new SubscriberList();
        list.setId(1L);
        Subscriber subscriber = new Subscriber();
        subscriber.setId(2L);

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));
        when(subscriberRepository.findById(2L)).thenReturn(Optional.of(subscriber));
        when(entryRepository.save(any(SubscriberListEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        SubscriberListEntry result = entryService.assignSubscriberToListAsAdmin(1L, 2L);

        assertEquals(list, result.getList());
        assertEquals(subscriber, result.getSubscriber());
        verify(entryRepository, times(1)).save(result);
    }
    @Test
    @DisplayName("Should return entries for current user")
    void testGetEntriesForCurrentUser() {
        SubscriberList list1 = new SubscriberList();
        SubscriberListEntry entry1 = new SubscriberListEntry();
        SubscriberListEntry entry2 = new SubscriberListEntry();
        list1.setEntries(Arrays.asList(entry1, entry2));
        testUser.setSubscriberLists(List.of(list1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        List<SubscriberListEntry> result = entryService.getEntriesForCurrentUser();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should delete entry for current user")
    void testDeleteEntryForCurrentUser() {
        SubscriberList list = new SubscriberList();
        list.setOwner(testUser);

        SubscriberListEntry entry = new SubscriberListEntry();
        entry.setList(list);

        when(entryRepository.findById(11L)).thenReturn(Optional.of(entry));

        entryService.deleteEntryForCurrentUser(11L);

        verify(entryRepository, times(1)).delete(entry);
    }

    @Test
    @DisplayName("Should throw exception when deleting entry not owned by user")
    void testDeleteEntryForCurrentUserForbidden() {
        User otherUser = new User();
        otherUser.setId(2L);

        SubscriberList list = new SubscriberList();
        list.setOwner(otherUser);

        SubscriberListEntry entry = new SubscriberListEntry();
        entry.setList(list);

        when(entryRepository.findById(22L)).thenReturn(Optional.of(entry));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entryService.deleteEntryForCurrentUser(22L);
        });

        assertEquals("Access denied to delete entry", exception.getMessage());
    }

    @Test
    @DisplayName("Should assign subscriber to user's own list")
    void testAssignSubscriberToMyList() {
        SubscriberList userList = new SubscriberList();
        userList.setId(100L);
        userList.setOwner(testUser);

        Subscriber existingSubscriber = new Subscriber();
        existingSubscriber.setId(200L);

        SubscriberListEntry existingEntry = new SubscriberListEntry();
        existingEntry.setSubscriber(existingSubscriber);
        userList.setEntries(List.of(existingEntry));

        when(listRepository.findById(100L)).thenReturn(Optional.of(userList));
        when(listRepository.findAll()).thenReturn(List.of(userList));
        when(subscriberRepository.findById(200L)).thenReturn(Optional.of(existingSubscriber));
        when(entryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubscriberListEntry result = entryService.assignSubscriberToMyList(100L, 200L);

        assertEquals(existingSubscriber, result.getSubscriber());
        assertEquals(userList, result.getList());
        verify(entryRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should throw exception if list not owned by user")
    void testAssignToListNotOwned() {
        SubscriberList list = new SubscriberList();
        list.setId(300L);
        list.setOwner(new User()); // not current user

        when(listRepository.findById(300L)).thenReturn(Optional.of(list));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entryService.assignSubscriberToMyList(300L, 1L);
        });

        assertEquals("List not found or not owned by current user", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception if subscriber not on any user list")
    void testAssignToListWithSubscriberNotFoundOnUserLists() {
        SubscriberList list = new SubscriberList();
        list.setId(400L);
        list.setOwner(testUser);
        list.setEntries(List.of()); // no matching subscriber

        when(listRepository.findById(400L)).thenReturn(Optional.of(list));
        when(listRepository.findAll()).thenReturn(List.of(list));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            entryService.assignSubscriberToMyList(400L, 99L);
        });

        assertEquals("Subscriber not found on any of your lists", exception.getMessage());
    }
}
