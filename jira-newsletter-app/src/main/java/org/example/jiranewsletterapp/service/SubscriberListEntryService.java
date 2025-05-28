package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberListEntryRepository;
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

    @Autowired
    public SubscriberListEntryService(SubscriberListEntryRepository entryRepository, UserRepository userRepository) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
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

}
