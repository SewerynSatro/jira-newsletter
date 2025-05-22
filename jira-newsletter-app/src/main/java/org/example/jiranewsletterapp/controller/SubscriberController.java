package org.example.jiranewsletterapp.controller;

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

    @GetMapping
    public List<Subscriber> getAll() {
        return subscriberService.getAllSubscribers();
    }

    @GetMapping("/{id}")
    public Subscriber getById(@PathVariable Long id) {
        return subscriberService.getById(id);
    }

    @PostMapping
    public Subscriber create(@RequestBody Subscriber subscriber) {
        return subscriberService.create(subscriber);
    }

    @PutMapping("/{id}")
    public Subscriber update(@PathVariable Long id, @RequestBody Subscriber updated) {
        return subscriberService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subscriberService.delete(id);
    }
}
