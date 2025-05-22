package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.example.jiranewsletterapp.repository.SubscriberListEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberListEntryService {

    private final SubscriberListEntryRepository entryRepository;

    @Autowired
    public SubscriberListEntryService(SubscriberListEntryRepository entryRepository) {
        this.entryRepository = entryRepository;
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

}
