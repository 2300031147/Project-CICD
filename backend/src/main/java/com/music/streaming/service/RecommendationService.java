package com.music.streaming.service;

import com.music.streaming.model.Song;
import com.music.streaming.repository.PlayHistoryRepository;
import com.music.streaming.repository.SongRepository;
import com.music.streaming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Song> getRecommendations(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's most played songs
        List<Object[]> mostPlayedSongs = playHistoryRepository.findMostPlayedSongsByUser(user.getId());
        
        if (mostPlayedSongs.isEmpty()) {
            // Return top songs if no history
            return songRepository.findTop10ByOrderByPlayCountDesc();
        }

        // Extract song IDs and get genres
        Set<String> preferredGenres = new HashSet<>();
        for (Object[] result : mostPlayedSongs) {
            Long songId = (Long) result[0];
            songRepository.findById(songId).ifPresent(song -> 
                preferredGenres.add(song.getGenre())
            );
        }

        // Get songs from preferred genres that user hasn't played much
        List<Song> recommendations = new ArrayList<>();
        for (String genre : preferredGenres) {
            recommendations.addAll(songRepository.findByGenre(genre));
        }

        // Remove duplicates and limit to 20
        return recommendations.stream()
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }
}
