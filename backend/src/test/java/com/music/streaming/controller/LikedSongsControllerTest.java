package com.music.streaming.controller;

import com.music.streaming.model.Artist;
import com.music.streaming.model.Song;
import com.music.streaming.model.User;
import com.music.streaming.repository.SongRepository;
import com.music.streaming.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LikedSongsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SongRepository songRepository;

    private User testUser;
    private Song testSong;
    private Artist testArtist;

    @BeforeEach
    public void setUp() {
        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Test Artist");

        testSong = new Song();
        testSong.setId(1L);
        testSong.setTitle("Test Song");
        testSong.setArtist(testArtist);
        testSong.setAlbum("Test Album");
        testSong.setGenre("Rock");
        testSong.setDuration(180);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setLikedSongs(new HashSet<>());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testLikeSong_Success() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/liked-songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Song liked successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUnlikeSong_Success() throws Exception {
        testUser.getLikedSongs().add(testSong);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(delete("/api/liked-songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Song unliked successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetLikedSongs() throws Exception {
        testUser.getLikedSongs().add(testSong);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/liked-songs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Song"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testIsSongLiked_True() throws Exception {
        testUser.getLikedSongs().add(testSong);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));

        mockMvc.perform(get("/api/liked-songs/1/is-liked"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testIsSongLiked_False() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));

        mockMvc.perform(get("/api/liked-songs/1/is-liked"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
