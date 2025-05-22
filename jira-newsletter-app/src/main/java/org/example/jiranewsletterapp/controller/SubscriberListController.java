package org.example.jiranewsletterapp.controller;

import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.service.SubscriberListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriber-lists")
public class SubscriberListController {

    private final SubscriberListService listService;

    @Autowired
    public SubscriberListController(SubscriberListService listService) {
        this.listService = listService;
    }

    @GetMapping
    public List<SubscriberList> getAll() {
        return listService.getAllLists();
    }

    @GetMapping("/{id}")
    public SubscriberList getById(@PathVariable Long id) {
        return listService.getById(id);
    }

    @PostMapping
    public SubscriberList create(@RequestBody SubscriberList list) {
        return listService.createList(list);
    }

    @PutMapping("/{id}")
    public SubscriberList update(@PathVariable Long id, @RequestBody SubscriberList updated) {
        return listService.updateList(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listService.deleteList(id);
    }
}
