package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.SubscriberList;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberListRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.example.jiranewsletterapp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<SubscriberList> getCurrentUserLists() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getSubscriberLists().size();

        return user.getSubscriberLists();
    }
    @Transactional(readOnly = true)
    public SubscriberList getMyListById(Long id) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return listRepository.findById(id)
                .filter(list -> list.getOwner().getId().equals(principal.getUser().getId()))
                .orElseThrow(() -> new RuntimeException("List not found or access denied"));
    }

    @Transactional
    public SubscriberList createListForCurrentUser(SubscriberList list) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (list.getOwner() != null) {
            throw new RuntimeException("Owner is added automatically for current user");
        }
        list.setOwner(user);

        return listRepository.save(list);
    }

    @Transactional
    public SubscriberList updateListForCurrentUser(Long id, SubscriberList updated) {
        SubscriberList existing = getMyListById(id);
        existing.setName(updated.getName());
        existing.getEntries().clear();
        existing.getEntries().addAll(updated.getEntries());
        return listRepository.save(existing);
    }

    @Transactional
    public void deleteListForCurrentUser(Long id) {
        SubscriberList list = getMyListById(id);
        listRepository.delete(list);
    }
}
