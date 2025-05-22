package org.example.jiranewsletterapp.repository;

import org.example.jiranewsletterapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
