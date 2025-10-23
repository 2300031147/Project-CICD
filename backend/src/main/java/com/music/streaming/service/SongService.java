package com.music.streaming.service;

import com.music.streaming.dto.SongDTO;
import com.music.streaming.model.Artist;
import com.music.streaming.model.PlayHistory;
import com.music.streaming.model.Song;
import com.music.streaming.model.User;
import com.music.streaming.repository.ArtistRepository;
import com.music.streaming.repository.PlayHistoryRepository;
import com.music.streaming.repository.SongRepository;
import com.music.streaming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SongDTO getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
        return convertToDTO(song);
    }

    public List<SongDTO> searchSongs(String keyword) {
        return songRepository.searchSongs(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getTopSongs() {
        return songRepository.findTop10ByOrderByPlayCountDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getSongsByArtist(Long artistId) {
        return songRepository.findByArtistId(artistId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SongDTO> getSongsByGenre(String genre) {
        return songRepository.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SongDTO createSong(SongDTO songDTO) {
        Artist artist = artistRepository.findById(songDTO.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Song song = new Song();
        song.setTitle(songDTO.getTitle());
        song.setArtist(artist);
        song.setAlbum(songDTO.getAlbum());
        song.setDuration(songDTO.getDuration());
        song.setGenre(songDTO.getGenre());
        song.setFileUrl(songDTO.getFileUrl());
        song.setCoverImageUrl(songDTO.getCoverImageUrl());
        song.setReleasedAt(LocalDateTime.now());

        Song savedSong = songRepository.save(song);
        return convertToDTO(savedSong);
    }

    @Transactional
    public void playSong(Long songId, String username) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Increment play count
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);

        // Record play history
        PlayHistory playHistory = new PlayHistory();
        playHistory.setSong(song);
        playHistory.setUser(user);
        playHistory.setDuration(song.getDuration());
        playHistoryRepository.save(playHistory);
    }

    @Transactional
    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }

    private SongDTO convertToDTO(Song song) {
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
    }
}
