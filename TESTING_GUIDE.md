# Admin Feature Testing Guide

This document provides instructions for manually testing the new admin features.

## Prerequisites

1. MySQL database running with `musicdb` database
2. Backend application running on port 8080
3. Frontend application running on port 3000
4. An admin user account

## Setup Admin User

1. Register a new user through the application UI
2. Connect to MySQL and update the user role:
   ```sql
   USE musicdb;
   UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
   ```

## Test Cases

### 1. Admin Panel Access

**Test**: Verify admin users can access the admin panel

**Steps**:
1. Login with admin credentials
2. Check if "Admin Panel" link appears in the sidebar
3. Click on the "Admin Panel" link
4. Verify you are redirected to `/admin` page

**Expected Result**: Admin panel loads successfully with dashboard view

---

### 2. Library Statistics

**Test**: Verify library statistics are displayed

**Steps**:
1. Navigate to admin panel
2. Check the statistics cards at the top

**Expected Result**: 
- Total Songs count displayed
- Total Artists count displayed
- Library Path displayed

---

### 3. Manual Song Addition

**Test**: Add a song manually

**Steps**:
1. Click "Add Song Manually" button
2. Fill in the form:
   - Title: "Test Song"
   - Artist: Select from dropdown
   - Album: "Test Album"
   - Duration: 180 (seconds)
   - Genre: "Test"
   - File URL: "file:///path/to/test.mp3"
   - Cover Image URL: (optional)
3. Click "Add" button

**Expected Result**: 
- Success message displayed
- Song appears in the songs table
- Modal closes

---

### 4. Edit Song

**Test**: Edit an existing song

**Steps**:
1. Click "Edit" button on any song in the table
2. Modify the title or other fields
3. Click "Update" button

**Expected Result**: 
- Success message displayed
- Changes reflected in the songs table

---

### 5. Delete Song

**Test**: Delete a song

**Steps**:
1. Click "Delete" button on a song
2. Confirm deletion in the dialog

**Expected Result**: 
- Success message displayed
- Song removed from the table

---

### 6. Library Scan

**Test**: Scan music library for new songs

**Steps**:
1. Place some audio files (MP3, WAV, FLAC, etc.) in the configured music folder
2. Click "Scan Library" button
3. Wait for the scan to complete

**Expected Result**: 
- Scan result box appears showing:
  - Number of scanned files
  - Number of imported songs
  - Number of skipped files
  - Any errors encountered
- New songs appear in the songs table

---

### 7. Security - Non-Admin Access

**Test**: Verify non-admin users cannot access admin endpoints

**Steps**:
1. Login with a regular (non-admin) user
2. Try to navigate to `/admin` directly via URL
3. Try to access `/api/admin/songs` endpoint

**Expected Result**: 
- Admin panel link not visible in sidebar for regular users
- Direct navigation to `/admin` should work but API calls should return 403 Forbidden
- Direct API calls return 403 Forbidden

---

### 8. Supported Audio Formats

**Test**: Verify different audio formats are supported

**Steps**:
1. Create a test folder with files of different formats:
   - .mp3
   - .wav
   - .flac
   - .aac
   - .m4a
   - .ogg
   - .wma
2. Update `music.library.path` in application.properties to point to this folder
3. Run library scan

**Expected Result**: 
- All supported formats are detected and imported
- Metadata extracted where available
- Files without metadata are imported with basic information

---

## API Endpoint Tests

### Test Admin Endpoints with cURL

Replace `YOUR_JWT_TOKEN` with an actual JWT token from an admin user.

#### Get All Songs (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/songs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Create Song
```bash
curl -X POST http://localhost:8080/api/admin/songs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Song",
    "artistId": 1,
    "album": "Test Album",
    "duration": 180,
    "genre": "Rock",
    "fileUrl": "file:///test.mp3",
    "coverImageUrl": "/images/default-cover.jpg"
  }'
```

#### Scan Library
```bash
curl -X POST http://localhost:8080/api/admin/library/scan \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get Library Stats
```bash
curl -X GET http://localhost:8080/api/admin/library/stats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Test Unauthorized Access (Should return 403)
```bash
curl -X GET http://localhost:8080/api/admin/songs
```

---

## Configuration Testing

### Test Different Library Paths

1. **Linux/Mac Path**:
   ```properties
   music.library.path=/home/user/Music
   ```

2. **Windows Path**:
   ```properties
   music.library.path=C:/Users/Username/Music/Project music
   ```

3. **Environment Variable**:
   ```bash
   export MUSIC_LIBRARY_PATH=/path/to/music
   ```

---

## Common Issues and Solutions

### Issue: Admin Panel not visible
**Solution**: Verify user has ADMIN role in database

### Issue: Library scan returns "Path does not exist"
**Solution**: 
- Check the configured path in application.properties
- Ensure the path exists and is accessible
- For Windows, use forward slashes or escaped backslashes

### Issue: Songs not importing
**Solution**: 
- Check file format is supported
- Verify files are not corrupted
- Check application logs for errors

### Issue: 403 Forbidden on admin endpoints
**Solution**: 
- Verify JWT token is valid
- Check user has ADMIN role
- Ensure token is included in Authorization header

---

## Notes

- The library scan is a long-running operation for large folders. Be patient during scanning.
- Songs are deduplicated by title and artist name to avoid duplicates.
- Artists are automatically created if they don't exist.
- The default cover image is used if no cover is provided.
- Metadata extraction works best with properly tagged audio files.
