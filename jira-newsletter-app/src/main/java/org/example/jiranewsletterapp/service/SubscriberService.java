package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.SubscriberRepository;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.example.jiranewsletterapp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository, UserRepository userRepository) {
        this.subscriberRepository = subscriberRepository;
        this.userRepository = userRepository;
    }

    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }

    public Subscriber getById(Long id) {
        return subscriberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with id: " + id));
    }

    public Subscriber create(Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    public Subscriber update(Long id, Subscriber updated) {
        updated.setId(id);
        return subscriberRepository.save(updated);
    }

    public void delete(Long id) {
        subscriberRepository.deleteById(id);
    }

    public void deleteByEmail(String email) {
        Subscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Subscriber not found with email: " + email));
        subscriberRepository.delete(subscriber);
    }

    @Transactional(readOnly = true)
    public List<Subscriber> getMySubscribers() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(principal.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSubscriberLists().stream()
                .flatMap(list -> list.getEntries().stream())
                .map(entry -> entry.getSubscriber())
                .distinct()
                .toList();
    }
}