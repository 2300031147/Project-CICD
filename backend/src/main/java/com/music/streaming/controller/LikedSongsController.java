package com.music.streaming.controller;

import com.music.streaming.dto.MessageResponse;
import com.music.streaming.dto.SongDTO;
import com.music.streaming.model.Song;
import com.music.streaming.model.User;
import com.music.streaming.repository.SongRepository;
import com.music.streaming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/liked-songs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LikedSongsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    @PostMapping("/{songId}")
    public ResponseEntity<MessageResponse> likeSong(@PathVariable Long songId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (user.getLikedSongs().contains(song)) {
            return ResponseEntity.ok(new MessageResponse("Song already liked"));
        }

        user.getLikedSongs().add(song);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Song liked successfully"));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<MessageResponse> unlikeSong(@PathVariable Long songId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (!user.getLikedSongs().contains(song)) {
            return ResponseEntity.ok(new MessageResponse("Song not in liked songs"));
        }

        user.getLikedSongs().remove(song);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Song unliked successfully"));
    }

    @GetMapping
    public ResponseEntity<List<SongDTO>> getLikedSongs(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SongDTO> likedSongs = user.getLikedSongs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(likedSongs);
    }

    @GetMapping("/{songId}/is-liked")
    public ResponseEntity<Boolean> isSongLiked(@PathVariable Long songId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        boolean isLiked = user.getLikedSongs().contains(song);
        return ResponseEntity.ok(isLiked);
    }

    private SongDTO convertToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setArtistId(song.getArtist().getId());
        dto.setArtistName(song.getArtist().getName());
        dto.setAlbum(song.getAlbum());
        dto.setDuration(song.getDuration());
        dto.setGenre(song.getGenre());
        dto.setFileUrl(song.getFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        dto.setPlayCount(song.getPlayCount());
        return dto;
    }
}
