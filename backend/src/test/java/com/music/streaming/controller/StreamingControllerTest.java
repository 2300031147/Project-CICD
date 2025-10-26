package com.music.streaming.controller;

import com.music.streaming.model.Artist;
import com.music.streaming.model.Song;
import com.music.streaming.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StreamingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongRepository songRepository;

    @TempDir
    Path tempDir;

    private Song testSong;
    private File testFile;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a temporary test file
        testFile = tempDir.resolve("test-song.mp3").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is a test audio file content");
        }

        // Create test artist and song
        Artist artist = new Artist();
        artist.setId(1L);
        artist.setName("Test Artist");

        testSong = new Song();
        testSong.setId(1L);
        testSong.setTitle("Test Song");
        testSong.setArtist(artist);
        testSong.setFileUrl("file://" + testFile.getAbsolutePath());
    }

    @Test
    public void testStreamSong_FullContent() throws Exception {
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));

        mockMvc.perform(get("/api/stream/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Accept-Ranges"))
                .andExpect(header().string("Accept-Ranges", "bytes"));
    }

    @Test
    public void testStreamSong_WithRangeRequest() throws Exception {
        when(songRepository.findById(1L)).thenReturn(Optional.of(testSong));

        mockMvc.perform(get("/api/stream/1")
                        .header("Range", "bytes=0-10"))
                .andExpect(status().isPartialContent())
                .andExpect(header().exists("Content-Range"))
                .andExpect(header().string("Accept-Ranges", "bytes"));
    }

    @Test
    public void testStreamSong_NotFound() throws Exception {
        when(songRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stream/999"))
                .andExpect(status().is5xxServerError());
    }
}
