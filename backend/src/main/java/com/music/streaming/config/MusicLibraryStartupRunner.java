package com.music.streaming.config;

import com.music.streaming.service.MusicLibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MusicLibraryStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MusicLibraryStartupRunner.class);

    @Value("${music.library.scan-on-startup:false}")
    private boolean scanOnStartup;

    @Autowired
    private MusicLibraryService musicLibraryService;

    @Override
    public void run(String... args) {
        if (scanOnStartup) {
            logger.info("Starting automatic library scan on startup...");
            try {
                Map<String, Object> result = musicLibraryService.scanLibrary();
                logger.info("Library scan completed: {}", result);
            } catch (Exception e) {
                logger.error("Error during startup library scan", e);
            }
        } else {
            logger.info("Automatic library scan on startup is disabled");
        }
    }
}
