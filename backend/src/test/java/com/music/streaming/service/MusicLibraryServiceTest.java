package com.music.streaming.service;

import com.music.streaming.model.Artist;
import com.music.streaming.model.Song;
import com.music.streaming.repository.ArtistRepository;
import com.music.streaming.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MusicLibraryServiceTest {

    @Autowired
    private MusicLibraryService musicLibraryService;

    @MockBean
    private SongRepository songRepository;

    @MockBean
    private ArtistRepository artistRepository;

    @TempDir
    Path tempDir;

    private Artist testArtist;
    private List<Song> existingSongs;

    @BeforeEach
    public void setUp() {
        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Test Artist");

        existingSongs = new ArrayList<>();
    }

    @Test
    public void testNormalizeString() throws Exception {
        // Use reflection to test the private normalizeString method
        Method normalizeMethod = MusicLibraryService.class.getDeclaredMethod("normalizeString", String.class);
        normalizeMethod.setAccessible(true);

        // Test basic normalization
        String result1 = (String) normalizeMethod.invoke(musicLibraryService, "Test Song");
        assertEquals("test song", result1);

        // Test with special characters
        String result2 = (String) normalizeMethod.invoke(musicLibraryService, "Test-Song!");
        assertEquals("testsong", result2);

        // Test with extra spaces
        String result3 = (String) normalizeMethod.invoke(musicLibraryService, "Test   Song");
        assertEquals("test song", result3);

        // Test with mixed case and punctuation
        String result4 = (String) normalizeMethod.invoke(musicLibraryService, "Test's Song!");
        assertEquals("tests song", result4);
    }

    @Test
    public void testIsDuplicate_WithNormalizedComparison() throws Exception {
        // Create an existing song
        Song existingSong = new Song();
        existingSong.setId(1L);
        existingSong.setTitle("Test Song");
        existingSong.setArtist(testArtist);
        existingSongs.add(existingSong);

        when(artistRepository.findByName("Test Artist")).thenReturn(Optional.of(testArtist));
        when(songRepository.findByArtist(testArtist)).thenReturn(existingSongs);

        // Use reflection to test the private isDuplicate method
        Method isDuplicateMethod = MusicLibraryService.class.getDeclaredMethod("isDuplicate", String.class, String.class);
        isDuplicateMethod.setAccessible(true);

        // Test exact match
        boolean result1 = (boolean) isDuplicateMethod.invoke(musicLibraryService, "Test Song", "Test Artist");
        assertTrue(result1, "Should detect exact duplicate");

        // Test case insensitive match
        boolean result2 = (boolean) isDuplicateMethod.invoke(musicLibraryService, "TEST SONG", "Test Artist");
        assertTrue(result2, "Should detect case insensitive duplicate");

        // Test with special characters
        boolean result3 = (boolean) isDuplicateMethod.invoke(musicLibraryService, "Test-Song!", "Test Artist");
        assertTrue(result3, "Should detect duplicate with different punctuation");

        // Test non-duplicate
        boolean result4 = (boolean) isDuplicateMethod.invoke(musicLibraryService, "Different Song", "Test Artist");
        assertFalse(result4, "Should not detect different song as duplicate");
    }

    @Test
    public void testScanLibrary_BasicOperation() throws Exception {
        // Create a test music directory
        Path musicDir = tempDir.resolve("music");
        Files.createDirectory(musicDir);

        // Set the library path
        ReflectionTestUtils.setField(musicLibraryService, "libraryPath", musicDir.toString());

        // Mock repository responses
        when(artistRepository.findByName(any())).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);
        when(songRepository.findByArtist(any())).thenReturn(new ArrayList<>());
        when(songRepository.save(any(Song.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Create a test MP3 file (just an empty file for testing)
        File testMp3 = musicDir.resolve("test.mp3").toFile();
        testMp3.createNewFile();

        // Scan the library
        var result = musicLibraryService.scanLibrary();

        // Verify results
        assertEquals("success", result.get("status"));
        assertTrue((Integer) result.get("scannedFiles") >= 1);
    }

    @Test
    public void testGetLibraryStats() {
        when(songRepository.count()).thenReturn(10L);
        when(artistRepository.count()).thenReturn(5L);

        var stats = musicLibraryService.getLibraryStats();

        assertEquals(10L, stats.get("totalSongs"));
        assertEquals(5L, stats.get("totalArtists"));
        assertNotNull(stats.get("libraryPath"));
        assertNotNull(stats.get("supportedFormats"));
    }
}
