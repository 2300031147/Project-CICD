package com.music.streaming.controller;

import com.music.streaming.dto.SongDTO;
import com.music.streaming.model.Song;
import com.music.streaming.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<SongDTO>> getRecommendations(Authentication authentication) {
        List<Song> songs = recommendationService.getRecommendations(authentication.getName());
        
        List<SongDTO> songDTOs = songs.stream()
                .map(song -> {
                    SongDTO dto = new SongDTO();
                    dto.setId(song.getId());
                    dto.setTitle(song.getTitle());
                    dto.setArtistName(song.getArtist().getName());
                    dto.setArtistId(song.getArtist().getId());
                    dto.setAlbum(song.getAlbum());
                    dto.setDuration(song.getDuration());
                    dto.setGenre(song.getGenre());
                    dto.setFileUrl(song.getFileUrl());
                    dto.setCoverImageUrl(song.getCoverImageUrl());
                    dto.setPlayCount(song.getPlayCount());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(songDTOs);
    }
}
