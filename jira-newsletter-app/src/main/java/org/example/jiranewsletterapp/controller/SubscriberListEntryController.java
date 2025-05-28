package org.example.jiranewsletterapp.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Wszystkie zapisy na listy", tags = {"Lists entries - Admin API"})
    @GetMapping
    public List<SubscriberListEntry> getAll() {
        return entryService.getAllEntries();
    }

    @Operation(summary = "Tworzenie nowego zapisu na liste", tags = {"Lists entries - Admin API"})
    @PostMapping
    public SubscriberListEntry create(@RequestBody SubscriberListEntry entry) {
        return entryService.createEntry(entry);
    }

    @Operation(summary = "Aktualizaca zapisu na liste po ID", tags = {"Lists entries - Admin API"})
    @PutMapping("/{id}")
    public SubscriberListEntry update(@PathVariable Long id, @RequestBody SubscriberListEntry updated) {
        return entryService.updateEntry(id, updated);
    }

    @Operation(summary = "Usuniecie zapisu na liste po ID", tags = {"Lists entries - Admin API"})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        entryService.deleteEntry(id);
    }

    @Operation(summary = "Zapis na liste po ID", tags = {"Lists entries - Admin API"})
    @GetMapping("/{id}")
    public SubscriberListEntry getById(@PathVariable Long id) {
        return entryService.getById(id);
    }

    @Operation(summary = "Wszystkie listy", tags = {"Lists entries - User API"})
    @GetMapping("/my")
    public List<SubscriberListEntry> getMyEntries() {
        return entryService.getEntriesForCurrentUser();
    }

    @Operation(summary = "Usuniecie zapisu na liste po ID", tags = {"Lists entries - User API"})
    @DeleteMapping("/my/{id}")
    public void deleteMyEntry(@PathVariable Long id) {
        entryService.deleteEntryForCurrentUser(id);
    }

}
