package org.example.jiranewsletterapp.controller;

import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {

    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberController(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @GetMapping
    public List<Subscriber> getAll() {
        return subscriberRepository.findAll();
    }

    @PostMapping
    public Subscriber create(@RequestBody Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    @PutMapping("/{id}")
    public Subscriber update(@PathVariable Long id, @RequestBody Subscriber updated) {
        updated.setId(id);
        return subscriberRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subscriberRepository.deleteById(id);
    }
}
