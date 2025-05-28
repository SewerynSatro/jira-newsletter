package org.example.jiranewsletterapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    @Autowired
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Operation(summary = "Wszyscy subskrybenci", tags = {"Subscribers - Admin API"})
    @GetMapping
    public List<Subscriber> getAll() {
        return subscriberService.getAllSubscribers();
    }

    @Operation(summary = "Pobierz subscribera po ID", tags = {"Subscribers - Admin API"})
    @GetMapping("/{id}")
    public Subscriber getById(@PathVariable Long id) {
        return subscriberService.getById(id);
    }

    @Operation(summary = "Stworz nowego subscribera", tags = {"Subscribers - Admin API"})
    @PostMapping
    public Subscriber create(@RequestBody Subscriber subscriber) {
        return subscriberService.create(subscriber);
    }

    @Operation(summary = "Zaktualizuj subscribera", tags = {"Subscribers - Admin API"})
    @PutMapping("/{id}")
    public Subscriber update(@PathVariable Long id, @RequestBody Subscriber updated) {
        return subscriberService.update(id, updated);
    }

    @Operation(summary = "Usun subscribera po ID", tags = {"Subscribers - Admin API"})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subscriberService.delete(id);
    }

    @Operation(summary = "Usun subscribera po email", tags = {"Subscribers - Admin API"})
    @DeleteMapping("/email/{email}")
    public void deleteByEmail(@PathVariable String email) {
        subscriberService.deleteByEmail(email);
    }

    @Operation(summary = "Wszyscy subskrybenci dla aktualnego uzytkownika", tags = {"Subscribers - User API"})
    @GetMapping("/my")
    public List<Subscriber> getMySubscribers() {
        return subscriberService.getMySubscribers();
    }
}
