package org.example.jiranewsletterapp.controller;

import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.example.jiranewsletterapp.service.SubscriberListEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriber-entries")
public class SubscriberListEntryController {

    private final SubscriberListEntryService entryService;

    @Autowired
    public SubscriberListEntryController(SubscriberListEntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public List<SubscriberListEntry> getAll() {
        return entryService.getAllEntries();
    }

    @PostMapping
    public SubscriberListEntry create(@RequestBody SubscriberListEntry entry) {
        return entryService.createEntry(entry);
    }

    @PutMapping("/{id}")
    public SubscriberListEntry update(@PathVariable Long id, @RequestBody SubscriberListEntry updated) {
        return entryService.updateEntry(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        entryService.deleteEntry(id);
    }

    @GetMapping("/{id}")
    public SubscriberListEntry getById(@PathVariable Long id) {
        return entryService.getById(id);
    }

}
