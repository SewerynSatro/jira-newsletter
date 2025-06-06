package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberListEntryRepository;
import org.example.jiranewsletterapp.repository.SubscriberListRepository;
import org.example.jiranewsletterapp.repository.SubscriberRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.example.jiranewsletterapp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberListEntryService {

    private final SubscriberListEntryRepository entryRepository;
    private final UserRepository userRepository;
    private final SubscriberListRepository listRepository;
    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberListEntryService(
            SubscriberListEntryRepository entryRepository,
            UserRepository userRepository,
            SubscriberListRepository listRepository,
            SubscriberRepository subscriberRepository
    ) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.listRepository = listRepository;
        this.subscriberRepository = subscriberRepository;
    }

    public List<SubscriberListEntry> getAllEntries() {
        return entryRepository.findAll();
    }

    public SubscriberListEntry createEntry(SubscriberListEntry entry) {
        return entryRepository.save(entry);
    }

    public SubscriberListEntry updateEntry(Long id, SubscriberListEntry updated) {
        updated.setId(id);
        return entryRepository.save(updated);
    }

    public void deleteEntry(Long id) {
        entryRepository.deleteById(id);
    }

    public SubscriberListEntry getById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<SubscriberListEntry> getEntriesForCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getSubscriberLists().forEach(list -> list.getEntries().size());

        return user.getSubscriberLists().stream()
                .flatMap(list -> list.getEntries().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEntryForCurrentUser(Long id) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();

        SubscriberListEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (!entry.getList().getOwner().getId().equals(userId)) {
            throw new RuntimeException("Access denied to delete entry");
        }

        entryRepository.delete(entry);
    }

    @Transactional
    public SubscriberListEntry assignSubscriberToListAsAdmin(Long listId, Long subscriberId) {
        SubscriberList targetList = listRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        SubscriberListEntry newEntry = new SubscriberListEntry();
        newEntry.setList(targetList);
        newEntry.setSubscriber(subscriber);
        return entryRepository.save(newEntry);
    }

    @Transactional
    public SubscriberListEntry assignSubscriberToMyList(Long listId, Long subscriberId) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = principal.getUser().getId();

        SubscriberList targetList = listRepository.findById(listId)
                .filter(list -> list.getOwner().getId().equals(currentUserId))
                .orElseThrow(() -> new RuntimeException("List not found or not owned by current user"));

        boolean subscriberExistsInUserLists = listRepository.findAll().stream()
                .filter(list -> list.getOwner().getId().equals(currentUserId))
                .flatMap(list -> list.getEntries().stream())
                .anyMatch(entry -> entry.getSubscriber().getId().equals(subscriberId));

        if (!subscriberExistsInUserLists) {
            throw new RuntimeException("Subscriber not found on any of your lists");
        }

        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        SubscriberListEntry newEntry = new SubscriberListEntry();
        newEntry.setList(targetList);
        newEntry.setSubscriber(subscriber);
        return entryRepository.save(newEntry);
    }
}
