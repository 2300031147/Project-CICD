package com.music.streaming.controller;

import com.music.streaming.model.Artist;
import com.music.streaming.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Artist>> searchArtists(@RequestParam String keyword) {
        return ResponseEntity.ok(artistService.searchArtists(keyword));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Artist>> getTopArtists() {
        return ResponseEntity.ok(artistService.getTopArtists());
    }

    @GetMapping("/following")
    public ResponseEntity<List<Artist>> getFollowedArtists(Authentication authentication) {
        return ResponseEntity.ok(artistService.getFollowedArtists(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<Artist> createArtist(@RequestBody Artist artist) {
        return ResponseEntity.ok(artistService.createArtist(artist));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followArtist(@PathVariable Long id, Authentication authentication) {
        artistService.followArtist(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowArtist(@PathVariable Long id, Authentication authentication) {
        artistService.unfollowArtist(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
