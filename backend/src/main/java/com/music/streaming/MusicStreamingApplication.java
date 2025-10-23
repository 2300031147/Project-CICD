package com.music.streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MusicStreamingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicStreamingApplication.class, args);
    }
}
