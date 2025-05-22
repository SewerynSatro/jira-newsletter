package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberListRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberListService {

    private final SubscriberListRepository listRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubscriberListService(SubscriberListRepository listRepository, UserRepository userRepository) {
        this.listRepository = listRepository;
        this.userRepository = userRepository;
    }

    public List<SubscriberList> getAllLists() {
        return listRepository.findAll();
    }

    public SubscriberList getById(Long id) {
        return listRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("List not found with id: " + id));
    }

    public SubscriberList createList(SubscriberList list) {
        User owner = userRepository.findById(list.getOwner().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        list.setOwner(owner);
        return listRepository.save(list);
    }

    public SubscriberList updateList(Long id, SubscriberList updated) {
        updated.setId(id);
        return listRepository.save(updated);
    }

    public void deleteList(Long id) {
        listRepository.deleteById(id);
    }
}
