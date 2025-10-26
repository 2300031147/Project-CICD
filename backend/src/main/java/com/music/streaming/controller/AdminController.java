package com.music.streaming.controller;

import com.music.streaming.dto.MessageResponse;
import com.music.streaming.dto.SongDTO;
import com.music.streaming.service.MusicLibraryService;
import com.music.streaming.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private SongService songService;

    @Autowired
    private MusicLibraryService musicLibraryService;

    @GetMapping("/songs")
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @PostMapping("/songs")
    public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO) {
        return ResponseEntity.ok(songService.createSong(songDTO));
    }

    @PutMapping("/songs/{id}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable Long id, @RequestBody SongDTO songDTO) {
        songDTO.setId(id);
        // For now, we'll delete and recreate (in production, implement proper update)
        songService.deleteSong(id);
        return ResponseEntity.ok(songService.createSong(songDTO));
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<MessageResponse> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok(new MessageResponse("Song deleted successfully"));
    }

    @PostMapping("/library/scan")
    public ResponseEntity<Map<String, Object>> scanLibrary() {
        Map<String, Object> result = musicLibraryService.scanLibrary();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/library/stats")
    public ResponseEntity<Map<String, Object>> getLibraryStats() {
        Map<String, Object> stats = musicLibraryService.getLibraryStats();
        return ResponseEntity.ok(stats);
    }
}
