package com.music.streaming.service;

import com.music.streaming.dto.PlaylistDTO;
import com.music.streaming.dto.SongDTO;
import com.music.streaming.model.Playlist;
import com.music.streaming.model.Song;
import com.music.streaming.model.User;
import com.music.streaming.repository.PlaylistRepository;
import com.music.streaming.repository.SongRepository;
import com.music.streaming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    public List<PlaylistDTO> getUserPlaylists(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return playlistRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PlaylistDTO> getPublicPlaylists() {
        return playlistRepository.findAllPublic().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PlaylistDTO getPlaylistById(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return convertToDTO(playlist);
    }

    @Transactional
    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = new Playlist();
        playlist.setName(playlistDTO.getName());
        playlist.setDescription(playlistDTO.getDescription());
        playlist.setCoverImageUrl(playlistDTO.getCoverImageUrl());
        playlist.setPublic(playlistDTO.isPublic());
        playlist.getUsers().add(user);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        user.getPlaylists().add(savedPlaylist);
        userRepository.save(user);

        return convertToDTO(savedPlaylist);
    }

    @Transactional
    public PlaylistDTO addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        playlist.getSongs().add(song);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }

    @Transactional
    public PlaylistDTO removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        playlist.getSongs().removeIf(song -> song.getId().equals(songId));
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }

    @Transactional
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }

    private PlaylistDTO convertToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());
        dto.setCoverImageUrl(playlist.getCoverImageUrl());
        dto.setPublic(playlist.isPublic());
        
        List<SongDTO> songDTOs = playlist.getSongs().stream()
                .map(song -> {
                    SongDTO songDTO = new SongDTO();
                    songDTO.setId(song.getId());
                    songDTO.setTitle(song.getTitle());
                    songDTO.setArtistName(song.getArtist().getName());
                    songDTO.setArtistId(song.getArtist().getId());
                    songDTO.setAlbum(song.getAlbum());
                    songDTO.setDuration(song.getDuration());
                    songDTO.setGenre(song.getGenre());
                    songDTO.setFileUrl(song.getFileUrl());
                    songDTO.setCoverImageUrl(song.getCoverImageUrl());
                    songDTO.setPlayCount(song.getPlayCount());
                    return songDTO;
                })
                .collect(Collectors.toList());
        
        dto.setSongs(songDTOs);
        return dto;
    }
}
