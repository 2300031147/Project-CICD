package com.music.streaming.controller;

import com.music.streaming.dto.PlaylistDTO;
import com.music.streaming.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @GetMapping("/my")
    public ResponseEntity<List<PlaylistDTO>> getMyPlaylists(Authentication authentication) {
        return ResponseEntity.ok(playlistService.getUserPlaylists(authentication.getName()));
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlaylistDTO>> getPublicPlaylists() {
        return ResponseEntity.ok(playlistService.getPublicPlaylists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO, 
                                                      Authentication authentication) {
        return ResponseEntity.ok(playlistService.createPlaylist(playlistDTO, authentication.getName()));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<PlaylistDTO> addSongToPlaylist(@PathVariable Long playlistId, 
                                                         @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.addSongToPlaylist(playlistId, songId));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<PlaylistDTO> removeSongFromPlaylist(@PathVariable Long playlistId, 
                                                              @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.removeSongFromPlaylist(playlistId, songId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }
}
