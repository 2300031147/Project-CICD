# New Features Implementation

This document describes the newly implemented features for the Music Streaming Platform.

## 1. Audio Streaming Endpoint with HTTP Range Support

### Overview
The platform now supports proper audio streaming with HTTP Range requests, enabling reliable playback in browsers and apps.

### Implementation Details
- **Endpoint**: `GET /api/stream/{songId}`
- **Features**:
  - Full file streaming
  - Partial content delivery (HTTP 206)
  - Range request support for seeking and resuming playback
  - Automatic content type detection
  - 1MB chunk size for efficient streaming
- **Security**: Public access (no authentication required for streaming)

### Supported Audio Formats
- MP3 (audio/mpeg)
- WAV (audio/wav)
- FLAC (audio/flac)
- M4A/AAC (audio/aac)
- OGG (audio/ogg)
- WMA (audio/x-ms-wma)

### Usage Example
```javascript
// Browser HTML5 Audio
const audio = new Audio(`http://localhost:8080/api/stream/123`);
audio.play();

// With fetch API for custom handling
fetch('http://localhost:8080/api/stream/123', {
  headers: {
    'Range': 'bytes=0-1023'
  }
})
```

### File Path Resolution
Songs stored with `file://` prefix in the database are automatically resolved to the correct filesystem path.

---

## 2. Automatic Library Scan on Startup

### Overview
The platform can now automatically scan and import songs from the configured music library directory when the application starts.

### Configuration
Add to `application.properties`:
```properties
music.library.scan-on-startup=true
music.library.path=/path/to/your/music
```

### Implementation Details
- **Component**: `MusicLibraryStartupRunner` (CommandLineRunner)
- **Behavior**:
  - Scans directory recursively for supported audio files
  - Extracts metadata using jaudiotagger
  - Creates artists and songs automatically
  - Skips duplicates
  - Logs scan results
- **Default**: Disabled (`scan-on-startup=false`)

### Scan Results
The startup scan will log:
- Number of files scanned
- Number of songs imported
- Number of files skipped
- Any errors encountered

---

## 3. Liked Songs Feature

### Overview
Users can now like/unlike songs and maintain a personal collection of favorite tracks.

### API Endpoints

#### Like a Song
```
POST /api/liked-songs/{songId}
Authorization: Bearer {jwt_token}
```
**Response**: `{ "message": "Song liked successfully" }`

#### Unlike a Song
```
DELETE /api/liked-songs/{songId}
Authorization: Bearer {jwt_token}
```
**Response**: `{ "message": "Song unliked successfully" }`

#### Get User's Liked Songs
```
GET /api/liked-songs
Authorization: Bearer {jwt_token}
```
**Response**: Array of SongDTO objects

#### Check if Song is Liked
```
GET /api/liked-songs/{songId}/is-liked
Authorization: Bearer {jwt_token}
```
**Response**: `true` or `false`

### Database Schema
A new many-to-many relationship table `user_liked_songs` stores the associations between users and their liked songs.

### Frontend Integration Example
```javascript
// Like a song
const likeSong = async (songId) => {
  await fetch(`/api/liked-songs/${songId}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
};

// Get liked songs
const getLikedSongs = async () => {
  const response = await fetch('/api/liked-songs', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await response.json();
};
```

---

## 4. Improved Duplicate Detection

### Overview
The music library scanner now uses normalized string comparison to detect duplicate songs more effectively.

### Implementation Details

#### Normalization Process
The system normalizes song titles and artist names by:
1. Converting to lowercase
2. Removing special characters and punctuation
3. Normalizing whitespace (multiple spaces → single space)
4. Trimming leading/trailing whitespace

#### Examples of Detected Duplicates
- "Test Song" = "TEST SONG" = "Test   Song"
- "test-song!" = "testsong" = "TestSong"
- "Artist's Name" = "Artists Name" = "ARTISTS NAME"
- "Song (feat. Artist)" = "Song feat Artist"

#### Benefits
- Prevents importing the same song with slight variations
- Handles metadata inconsistencies from different sources
- Reduces database bloat from duplicate entries
- Maintains cleaner library organization

### Algorithm
```java
private String normalizeString(String input) {
    return input.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")  // Remove special chars
            .replaceAll("\\s+", " ")          // Normalize whitespace
            .trim();
}
```

When importing a song, the system:
1. Normalizes the title and artist name
2. Queries existing songs by the same artist
3. Compares normalized titles
4. Skips import if a match is found

---

## Testing

### Unit Tests
The implementation includes comprehensive unit tests:
- `StreamingControllerTest`: Tests streaming with and without Range headers
- `LikedSongsControllerTest`: Tests like/unlike and retrieval operations
- `MusicLibraryServiceTest`: Tests normalization and duplicate detection

### Running Tests
```bash
cd backend
mvn test
```

### Test Database
Tests use H2 in-memory database configured in `src/test/resources/application.properties`.

---

## Security Considerations

### Streaming Endpoint
- Public access to allow media playback without constant authentication
- Files are served from configured directory only
- Path traversal protection through file path validation

### Liked Songs Endpoints
- All endpoints require JWT authentication
- Users can only access their own liked songs
- User identity validated through authentication token

### Library Scanning
- Requires ADMIN role to trigger manual scans via API
- Automatic startup scan is a configuration option
- Scans are logged for audit purposes

---

## Migration Notes

### Database Changes
The implementation adds:
- `user_liked_songs` table (many-to-many relationship)
- No changes to existing tables or data

### Backward Compatibility
- Existing songs continue to work
- No breaking changes to existing APIs
- New endpoints are additive only

### Deployment
1. Update application.properties with desired configuration
2. Deploy new backend JAR
3. Database schema updates via Hibernate auto-update
4. No manual SQL scripts required

---

## Future Enhancements

Potential improvements for future releases:
1. Batch operations for liked songs
2. Export/import liked songs playlist
3. Collaborative filtering based on liked songs
4. "Recently liked" timeline
5. Like counts and popularity metrics
6. Share liked songs with other users
7. Smart playlists based on liked songs
8. Advanced streaming features (HLS/DASH, transcoding)

---

## Support

For issues or questions:
- Check application logs for detailed error messages
- Review security configuration for access issues
- Ensure music library path is correctly configured
- Verify JWT token validity for authenticated endpoints
