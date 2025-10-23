package com.music.streaming.service;

import com.music.streaming.model.Artist;
import com.music.streaming.model.User;
import com.music.streaming.repository.ArtistRepository;
import com.music.streaming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Artist getArtistById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
    }

    public List<Artist> searchArtists(String keyword) {
        return artistRepository.searchArtists(keyword);
    }

    public List<Artist> getTopArtists() {
        return artistRepository.findTop10ByOrderByFollowerCountDesc();
    }

    @Transactional
    public Artist createArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    @Transactional
    public void followArtist(Long artistId, String username) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getFollowingArtists().add(artist);
        artist.setFollowerCount(artist.getFollowerCount() + 1);
        
        userRepository.save(user);
        artistRepository.save(artist);
    }

    @Transactional
    public void unfollowArtist(Long artistId, String username) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getFollowingArtists().remove(artist);
        artist.setFollowerCount(Math.max(0, artist.getFollowerCount() - 1));
        
        userRepository.save(user);
        artistRepository.save(artist);
    }

    public List<Artist> getFollowedArtists(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getFollowingArtists().stream().toList();
    }
}
