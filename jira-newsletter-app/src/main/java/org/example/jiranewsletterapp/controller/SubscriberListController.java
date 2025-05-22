package org.example.jiranewsletterapp.controller;

import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberListRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriber-lists")
public class SubscriberListController {

    private final SubscriberListRepository listRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubscriberListController(SubscriberListRepository listRepository, UserRepository userRepository) {
        this.listRepository = listRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<SubscriberList> getAll() {
        return listRepository.findAll();
    }

    @PostMapping
    public SubscriberList create(@RequestBody SubscriberList list) {
        if (list.getOwner() == null || list.getOwner().getId() == null) {
            throw new RuntimeException("Owner must be provided with a valid ID.");
        }

        Long ownerId = list.getOwner().getId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));

        list.setOwner(owner);
        return listRepository.save(list);
    }

    @PutMapping("/{id}")
    public SubscriberList update(@PathVariable Long id, @RequestBody SubscriberList updated) {
        updated.setId(id);
        return listRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listRepository.deleteById(id);
    }
}
