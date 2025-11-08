package com.music.streaming.config;

import com.music.streaming.model.User;
import com.music.streaming.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "Rithesh";
            String adminPassword = "Rithesh@11";
            String adminEmail = "ritheshravuri@gmail.com"; // must be unique

            userRepository.findByUsername(adminUsername).orElseGet(() -> {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setFirstName("Rithesh");
                admin.setLastName("");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(User.Role.ADMIN);
                admin.setEnabled(true);
                User saved = userRepository.save(admin);
                log.info("Created default admin user '{}' with role {}", saved.getUsername(), saved.getRole());
                return saved;
            });
        };
    }
}
