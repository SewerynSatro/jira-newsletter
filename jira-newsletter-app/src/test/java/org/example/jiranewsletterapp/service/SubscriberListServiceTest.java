package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberListRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
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

public class SubscriberListServiceTest {

    private SubscriberListRepository listRepository;
    private UserRepository userRepository;
    private SubscriberListService listService;

    private User testUser;

    @BeforeEach
    void setUp() {
        listRepository = mock(SubscriberListRepository.class);
        userRepository = mock(UserRepository.class);
        listService = new SubscriberListService(listRepository, userRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");

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
    @DisplayName("Should return all subscriber lists")
    void testGetAllLists() {
        SubscriberList list1 = new SubscriberList();
        list1.setName("List A");

        SubscriberList list2 = new SubscriberList();
        list2.setName("List B");

        when(listRepository.findAll()).thenReturn(Arrays.asList(list1, list2));

        List<SubscriberList> result = listService.getAllLists();

        assertEquals(2, result.size());
        verify(listRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return subscriber list by ID")
    void testGetById() {
        SubscriberList list = new SubscriberList();
        list.setName("VIP List");

        when(listRepository.findById(1L)).thenReturn(Optional.of(list));

        SubscriberList result = listService.getById(1L);

        assertEquals("VIP List", result.getName());
        verify(listRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception if list by ID not found")
    void testGetByIdNotFound() {
        when(listRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listService.getById(42L);
        });

        assertEquals("List not found with id: 42", exception.getMessage());
        verify(listRepository, times(1)).findById(42L);
    }

    @Test
    @DisplayName("Should create new subscriber list")
    void testCreateList() {
        User owner = new User();
        owner.setId(1L);

        SubscriberList list = new SubscriberList();
        list.setName("Newsletter");
        list.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(listRepository.save(list)).thenReturn(list);

        SubscriberList result = listService.createList(list);

        assertEquals("Newsletter", result.getName());
        assertEquals(1L, result.getOwner().getId());
        verify(userRepository, times(1)).findById(1L);
        verify(listRepository, times(1)).save(list);
    }

    @Test
    @DisplayName("Should throw exception if owner not found while creating list")
    void testCreateListOwnerNotFound() {
        User missingOwner = new User();
        missingOwner.setId(99L);

        SubscriberList list = new SubscriberList();
        list.setOwner(missingOwner);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listService.createList(list);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(listRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update subscriber list")
    void testUpdateList() {
        SubscriberList updated = new SubscriberList();
        updated.setName("Updated Name");

        when(listRepository.save(updated)).thenReturn(updated);

        SubscriberList result = listService.updateList(1L, updated);

        assertEquals("Updated Name", result.getName());
        assertEquals(1L, result.getId());
        verify(listRepository, times(1)).save(updated);
    }

    @Test
    @DisplayName("Should delete subscriber list by ID")
    void testDeleteList() {
        listService.deleteList(7L);

        verify(listRepository, times(1)).deleteById(7L);
    }

    @Test
    @DisplayName("Should return all subscriber lists for current user")
    void testGetCurrentUserLists() {
        SubscriberList list1 = new SubscriberList();
        list1.setName("List A");
        list1.setOwner(testUser);

        SubscriberList list2 = new SubscriberList();
        list2.setName("List B");
        list2.setOwner(testUser);

        testUser.setSubscriberLists(Arrays.asList(list1, list2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        List<SubscriberList> result = listService.getCurrentUserLists();

        assertEquals(2, result.size());
        assertEquals("List A", result.get(0).getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return list by ID for current user")
    void testGetMyListById() {
        SubscriberList list = new SubscriberList();
        list.setId(5L);
        list.setName("My List");
        list.setOwner(testUser);

        when(listRepository.findById(5L)).thenReturn(Optional.of(list));

        SubscriberList result = listService.getMyListById(5L);

        assertEquals("My List", result.getName());
        verify(listRepository, times(1)).findById(5L);
    }

    @Test
    @DisplayName("Should throw exception when list not found or not owned")
    void testGetMyListByIdForbidden() {
        SubscriberList list = new SubscriberList();
        list.setId(6L);
        User anotherUser = new User();
        anotherUser.setId(999L);
        list.setOwner(anotherUser);

        when(listRepository.findById(6L)).thenReturn(Optional.of(list));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listService.getMyListById(6L);
        });

        assertEquals("List not found or access denied", exception.getMessage());
    }

    @Test
    @DisplayName("Should create new list for current user")
    void testCreateListForCurrentUser() {
        SubscriberList newList = new SubscriberList();
        newList.setName("New List");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(listRepository.save(any(SubscriberList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubscriberList result = listService.createListForCurrentUser(newList);

        assertEquals("New List", result.getName());
        assertEquals(testUser, result.getOwner());
        verify(listRepository, times(1)).save(result);
    }

    @Test
    @DisplayName("Should throw exception if user tries to set owner manually")
    void testCreateListWithOwnerSet() {
        SubscriberList list = new SubscriberList();
        list.setName("Injected Owner");
        list.setOwner(new User());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            listService.createListForCurrentUser(list);
        });

        assertEquals("Owner is added automatically for current user", exception.getMessage());
        verify(listRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update list for current user")
    void testUpdateListForCurrentUser() {
        SubscriberList existing = new SubscriberList();
        existing.setId(10L);
        existing.setName("Old Name");
        existing.setOwner(testUser);

        SubscriberList updated = new SubscriberList();
        updated.setName("Updated Name");

        when(listRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(listRepository.save(existing)).thenReturn(existing);

        SubscriberList result = listService.updateListForCurrentUser(10L, updated);

        assertEquals("Updated Name", result.getName());
        verify(listRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Should delete list for current user")
    void testDeleteListForCurrentUser() {
        SubscriberList list = new SubscriberList();
        list.setId(20L);
        list.setOwner(testUser);

        when(listRepository.findById(20L)).thenReturn(Optional.of(list));

        listService.deleteListForCurrentUser(20L);

        verify(listRepository, times(1)).delete(list);
    }
}
