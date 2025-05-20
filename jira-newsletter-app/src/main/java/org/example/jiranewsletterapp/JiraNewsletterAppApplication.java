package org.example.jiranewsletterapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("org.example.jiranewsletterapp.entity")
@EnableJpaRepositories("org.example.jiranewsletterapp.repository")
@SpringBootApplication
public class JiraNewsletterAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiraNewsletterAppApplication.class, args);
    }

}
