package org.example.jiranewsletterapp.controller;

import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.example.jiranewsletterapp.repository.SubscriberListEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriber-entries")
public class SubscriberListEntryController {

    private final SubscriberListEntryRepository entryRepository;

    @Autowired
    public SubscriberListEntryController(SubscriberListEntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @GetMapping
    public List<SubscriberListEntry> getAll() {
        return entryRepository.findAll();
    }

    @PostMapping
    public SubscriberListEntry create(@RequestBody SubscriberListEntry entry) {
        return entryRepository.save(entry);
    }

    @PutMapping("/{id}")
    public SubscriberListEntry update(@PathVariable Long id, @RequestBody SubscriberListEntry updated) {
        updated.setId(id);
        return entryRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        entryRepository.deleteById(id);
    }
}
