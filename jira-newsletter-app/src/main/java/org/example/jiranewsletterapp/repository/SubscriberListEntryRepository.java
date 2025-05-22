package org.example.jiranewsletterapp.repository;

import org.example.jiranewsletterapp.entity.SubscriberListEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberListEntryRepository extends JpaRepository<SubscriberListEntry, Long> {
}
