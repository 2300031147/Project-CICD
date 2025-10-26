package com.music.streaming.controller;

import com.music.streaming.model.Song;
import com.music.streaming.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/stream")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StreamingController {

    private static final Logger logger = LoggerFactory.getLogger(StreamingController.class);
    private static final long CHUNK_SIZE = 1024 * 1024; // 1MB chunks

    @Autowired
    private SongRepository songRepository;

    @GetMapping("/{songId}")
    public ResponseEntity<Resource> streamSong(
            @PathVariable Long songId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        
        try {
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Song not found"));

            // Extract file path from fileUrl (remove "file://" prefix if present)
            String filePath = song.getFileUrl();
            if (filePath.startsWith("file://")) {
                filePath = filePath.substring(7);
            }

            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                logger.error("File not found or cannot be read: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            long fileSize = file.length();
            String contentType = determineContentType(file);

            // Handle Range requests for partial content
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                return handleRangeRequest(file, fileSize, contentType, rangeHeader);
            }

            // Full file response
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(resource);

        } catch (Exception e) {
            logger.error("Error streaming song: {}", songId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Resource> handleRangeRequest(File file, long fileSize, String contentType, String rangeHeader) {
        try {
            // Parse range header (e.g., "bytes=0-1023")
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            long rangeStart = Long.parseLong(ranges[0]);
            long rangeEnd;
            
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = Math.min(rangeStart + CHUNK_SIZE - 1, fileSize - 1);
            }

            // Validate range
            if (rangeStart >= fileSize || rangeStart < 0 || rangeEnd >= fileSize) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                        .build();
            }

            long contentLength = rangeEnd - rangeStart + 1;

            // Create partial file resource
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(rangeStart);
            byte[] buffer = new byte[(int) contentLength];
            randomAccessFile.read(buffer);
            randomAccessFile.close();

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(new org.springframework.core.io.ByteArrayResource(buffer));

        } catch (Exception e) {
            logger.error("Error handling range request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(File file) {
        try {
            Path path = Paths.get(file.getAbsolutePath());
            String contentType = Files.probeContentType(path);
            if (contentType != null) {
                return contentType;
            }
        } catch (IOException e) {
            logger.warn("Could not determine content type for file: {}", file.getName());
        }

        // Fallback based on file extension
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".mp3")) return "audio/mpeg";
        if (fileName.endsWith(".wav")) return "audio/wav";
        if (fileName.endsWith(".flac")) return "audio/flac";
        if (fileName.endsWith(".m4a") || fileName.endsWith(".aac")) return "audio/aac";
        if (fileName.endsWith(".ogg")) return "audio/ogg";
        if (fileName.endsWith(".wma")) return "audio/x-ms-wma";
        
        return "application/octet-stream";
    }
}
