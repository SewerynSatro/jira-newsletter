package org.example.jiranewsletterapp.service;

import org.example.jiranewsletterapp.entity.Subscriber;
import org.example.jiranewsletterapp.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
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
}