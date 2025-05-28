package org.example.jiranewsletterapp.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Wszystkie listy", tags = {"Lists - Admin API"})
    @GetMapping
    public List<SubscriberList> getAll() {
        return listService.getAllLists();
    }

    @Operation(summary = "Lista przez ID", tags = {"Lists - Admin API"})
    @GetMapping("/{id}")
    public SubscriberList getById(@PathVariable Long id) {
        return listService.getById(id);
    }

    @Operation(summary = "Tworzenie listy", tags = {"Lists - Admin API"})
    @PostMapping
    public SubscriberList create(@RequestBody SubscriberList list) {
        return listService.createList(list);
    }

    @Operation(summary = "Aktualizacja listy po ID", tags = {"Lists - Admin API"})
    @PutMapping("/{id}")
    public SubscriberList update(@PathVariable Long id, @RequestBody SubscriberList updated) {
        return listService.updateList(id, updated);
    }

    @Operation(summary = "Usuniecie listy po ID", tags = {"Lists - Admin API"})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listService.deleteList(id);
    }

    @Operation(summary = "Wszystkie listy", tags = {"Lists - User API"})
    @GetMapping("/my")
    public List<SubscriberList> getMyLists() {
        return listService.getCurrentUserLists();
    }

    @Operation(summary = "Wszystkie listy", tags = {"Lists - User API"})
    @GetMapping("/my/{id}")
    public SubscriberList getMyListById(@PathVariable Long id) {
        return listService.getMyListById(id);
    }

    @Operation(summary = "Tworzenie listy", tags = {"Lists - User API"})
    @PostMapping("/my")
    public SubscriberList createMyList(@RequestBody SubscriberList list) {
        return listService.createListForCurrentUser(list);
    }

    @Operation(summary = "Aktualizacja listy po ID", tags = {"Lists - User API"})
    @PutMapping("/my/{id}")
    public SubscriberList updateMyList(@PathVariable Long id, @RequestBody SubscriberList updatedList) {
        return listService.updateListForCurrentUser(id, updatedList);
    }

    @Operation(summary = "Usuniecie listy po ID", tags = {"Lists - User API"})
    @DeleteMapping("/my/{id}")
    public void deleteMyList(@PathVariable Long id) {
        listService.deleteListForCurrentUser(id);
    }

}
