package org.example.jiranewsletterapp.repository;

import org.example.jiranewsletterapp.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
}
