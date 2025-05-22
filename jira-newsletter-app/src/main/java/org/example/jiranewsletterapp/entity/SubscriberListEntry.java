package org.example.jiranewsletterapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SubscriberListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateAdded = LocalDateTime.now();

    private boolean confirmed = false;

    private String source;

    @ManyToOne
    @JoinColumn(name = "subscriber_list_id")
    @JsonIgnore
    private SubscriberList list;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;
}
