package com.music.streaming.service;

import com.music.streaming.model.Artist;
import com.music.streaming.model.Song;
import com.music.streaming.repository.ArtistRepository;
import com.music.streaming.repository.SongRepository;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
public class MusicLibraryService {

    private static final Logger logger = LoggerFactory.getLogger(MusicLibraryService.class);
    
    private static final Set<String> SUPPORTED_FORMATS = Set.of(
        "mp3", "wav", "flac", "aac", "m4a", "aiff", "aif", "alac", "ogg", "wma"
    );

    @Value("${music.library.path}")
    private String libraryPath;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Transactional
    public Map<String, Object> scanLibrary() {
        logger.info("Starting library scan from: {}", libraryPath);
        
        Map<String, Object> result = new HashMap<>();
        int scannedFiles = 0;
        int importedSongs = 0;
        int skippedFiles = 0;
        List<String> errors = new ArrayList<>();

        try {
            Path libraryDir = Paths.get(libraryPath);
            
            if (!Files.exists(libraryDir)) {
                logger.warn("Library path does not exist: {}", libraryPath);
                result.put("status", "error");
                result.put("message", "Library path does not exist: " + libraryPath);
                return result;
            }

            try (Stream<Path> paths = Files.walk(libraryDir)) {
                List<Path> audioFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedAudioFile)
                    .toList();

                for (Path filePath : audioFiles) {
                    scannedFiles++;
                    try {
                        if (importSongFromFile(filePath.toFile())) {
                            importedSongs++;
                        } else {
                            skippedFiles++;
                        }
                    } catch (Exception e) {
                        logger.error("Error importing file: {}", filePath, e);
                        errors.add(filePath.getFileName().toString() + ": " + e.getMessage());
                        skippedFiles++;
                    }
                }
            }

            result.put("status", "success");
            result.put("scannedFiles", scannedFiles);
            result.put("importedSongs", importedSongs);
            result.put("skippedFiles", skippedFiles);
            result.put("errors", errors);
            
            logger.info("Library scan completed. Scanned: {}, Imported: {}, Skipped: {}", 
                       scannedFiles, importedSongs, skippedFiles);

        } catch (Exception e) {
            logger.error("Error during library scan", e);
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    private boolean isSupportedAudioFile(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        return SUPPORTED_FORMATS.stream().anyMatch(ext -> filename.endsWith("." + ext));
    }

    @Transactional
    private boolean importSongFromFile(File file) {
        try {
            // Extract metadata using jaudiotagger
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            
            String title = getTagField(tag, FieldKey.TITLE, file.getName());
            String artistName = getTagField(tag, FieldKey.ARTIST, "Unknown Artist");
            String album = getTagField(tag, FieldKey.ALBUM, "Unknown Album");
            String genre = getTagField(tag, FieldKey.GENRE, "Unknown");
            int duration = audioFile.getAudioHeader().getTrackLength();

            // Check if song already exists
            Optional<Song> existingSong = songRepository.findByTitleAndArtist_Name(title, artistName);
            if (existingSong.isPresent()) {
                logger.debug("Song already exists: {} by {}", title, artistName);
                return false;
            }

            // Get or create artist
            Artist artist = artistRepository.findByName(artistName)
                .orElseGet(() -> {
                    Artist newArtist = new Artist();
                    newArtist.setName(artistName);
                    newArtist.setBio("Auto-imported artist");
                    newArtist.setCountry("Unknown");
                    return artistRepository.save(newArtist);
                });

            // Create song
            Song song = new Song();
            song.setTitle(title);
            song.setArtist(artist);
            song.setAlbum(album);
            song.setGenre(genre);
            song.setDuration(duration);
            song.setFileUrl("file://" + file.getAbsolutePath());
            song.setCoverImageUrl("/images/default-cover.jpg");
            song.setPlayCount(0);
            song.setReleasedAt(LocalDateTime.now());

            songRepository.save(song);
            logger.info("Imported song: {} by {} from {}", title, artistName, file.getName());
            return true;

        } catch (Exception e) {
            logger.error("Error reading metadata from file: {}", file.getName(), e);
            // Create basic entry for files without proper metadata
            return importSongWithBasicInfo(file);
        }
    }

    private boolean importSongWithBasicInfo(File file) {
        try {
            String filename = file.getName();
            String title = filename.substring(0, filename.lastIndexOf('.'));
            String artistName = "Unknown Artist";

            // Check if already exists
            Optional<Song> existingSong = songRepository.findByTitleAndArtist_Name(title, artistName);
            if (existingSong.isPresent()) {
                return false;
            }

            // Get or create unknown artist
            Artist artist = artistRepository.findByName(artistName)
                .orElseGet(() -> {
                    Artist newArtist = new Artist();
                    newArtist.setName(artistName);
                    newArtist.setBio("Default artist for songs without metadata");
                    newArtist.setCountry("Unknown");
                    return artistRepository.save(newArtist);
                });

            Song song = new Song();
            song.setTitle(title);
            song.setArtist(artist);
            song.setAlbum("Unknown Album");
            song.setGenre("Unknown");
            song.setDuration(180); // Default 3 minutes
            song.setFileUrl("file://" + file.getAbsolutePath());
            song.setCoverImageUrl("/images/default-cover.jpg");
            song.setPlayCount(0);
            song.setReleasedAt(LocalDateTime.now());

            songRepository.save(song);
            return true;
        } catch (Exception e) {
            logger.error("Error creating basic song entry: {}", file.getName(), e);
            return false;
        }
    }

    private String getTagField(Tag tag, FieldKey key, String defaultValue) {
        if (tag == null) {
            return defaultValue;
        }
        try {
            String value = tag.getFirst(key);
            return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Map<String, Object> getLibraryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSongs", songRepository.count());
        stats.put("totalArtists", artistRepository.count());
        stats.put("libraryPath", libraryPath);
        stats.put("supportedFormats", SUPPORTED_FORMATS);
        return stats;
    }
}
