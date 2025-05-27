package org.example.jiranewsletterapp.config;

import org.example.jiranewsletterapp.entity.Role;
import org.example.jiranewsletterapp.entity.User;
import org.example.jiranewsletterapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin1@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin1@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Root");
            admin.setPassword(passwordEncoder.encode("secret123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
