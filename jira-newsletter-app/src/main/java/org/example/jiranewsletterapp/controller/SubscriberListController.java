package org.example.jiranewsletterapp.controller;

import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.repository.SubscriberListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriber-lists")
public class SubscriberListController {

    private final SubscriberListRepository listRepository;

    @Autowired
    public SubscriberListController(SubscriberListRepository listRepository) {
        this.listRepository = listRepository;
    }

    @GetMapping
    public List<SubscriberList> getAll() {
        return listRepository.findAll();
    }

    @PostMapping
    public SubscriberList create(@RequestBody SubscriberList list) {
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
