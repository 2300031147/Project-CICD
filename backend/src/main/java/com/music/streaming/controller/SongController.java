package com.music.streaming.controller;

import com.music.streaming.dto.SongDTO;
import com.music.streaming.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String keyword) {
        return ResponseEntity.ok(songService.searchSongs(keyword));
    }

    @GetMapping("/top")
    public ResponseEntity<List<SongDTO>> getTopSongs() {
        return ResponseEntity.ok(songService.getTopSongs());
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<SongDTO>> getSongsByArtist(@PathVariable Long artistId) {
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<SongDTO>> getSongsByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(songService.getSongsByGenre(genre));
    }

    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(songService.createSong(songDTO));
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<Void> playSong(@PathVariable Long id, Authentication authentication) {
        songService.playSong(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok().build();
    }
}
