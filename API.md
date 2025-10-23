# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All authenticated endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Authentication Endpoints

### Register User
**POST** `/auth/signup`

Register a new user account.

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:** `200 OK`
```json
{
  "message": "User registered successfully!"
}
```

**Error Response:** `400 Bad Request`
```json
{
  "message": "Error: Username is already taken!"
}
```

---

### Login
**POST** `/auth/signin`

Authenticate a user and receive a JWT token.

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "USER"
}
```

---

## Song Endpoints

### Get All Songs
**GET** `/songs`

Returns all available songs.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Hey Jude",
    "artistName": "The Beatles",
    "artistId": 1,
    "album": "The Beatles 1967-1970",
    "duration": 431,
    "genre": "Rock",
    "fileUrl": "/music/hey-jude.mp3",
    "coverImageUrl": "/images/beatles-1967-1970.jpg",
    "playCount": 15000
  }
]
```

---

### Get Song by ID
**GET** `/songs/{id}`

Get details of a specific song.

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Hey Jude",
  "artistName": "The Beatles",
  "artistId": 1,
  "album": "The Beatles 1967-1970",
  "duration": 431,
  "genre": "Rock",
  "fileUrl": "/music/hey-jude.mp3",
  "coverImageUrl": "/images/beatles-1967-1970.jpg",
  "playCount": 15000
}
```

---

### Search Songs
**GET** `/songs/search?keyword={keyword}`

Search for songs by title, artist name, or album.

**Query Parameters:**
- `keyword` (required): Search term

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Hey Jude",
    "artistName": "The Beatles",
    "artistId": 1,
    "album": "The Beatles 1967-1970",
    "duration": 431,
    "genre": "Rock",
    "fileUrl": "/music/hey-jude.mp3",
    "coverImageUrl": "/images/beatles-1967-1970.jpg",
    "playCount": 15000
  }
]
```

---

### Get Top Songs
**GET** `/songs/top`

Get the top 10 most played songs.

**Response:** `200 OK`
```json
[
  {
    "id": 9,
    "title": "Shape of You",
    "artistName": "Ed Sheeran",
    "artistId": 5,
    "album": "÷ (Divide)",
    "duration": 233,
    "genre": "Pop",
    "fileUrl": "/music/shape-of-you.mp3",
    "coverImageUrl": "/images/divide.jpg",
    "playCount": 35000
  }
]
```

---

### Get Songs by Artist
**GET** `/songs/artist/{artistId}`

Get all songs by a specific artist.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Hey Jude",
    "artistName": "The Beatles",
    "artistId": 1,
    "album": "The Beatles 1967-1970",
    "duration": 431,
    "genre": "Rock",
    "fileUrl": "/music/hey-jude.mp3",
    "coverImageUrl": "/images/beatles-1967-1970.jpg",
    "playCount": 15000
  }
]
```

---

### Get Songs by Genre
**GET** `/songs/genre/{genre}`

Get all songs in a specific genre.

**Response:** `200 OK`

---

### Create Song
**POST** `/songs` 🔒

Create a new song entry.

**Request Body:**
```json
{
  "title": "New Song",
  "artistId": 1,
  "album": "New Album",
  "duration": 180,
  "genre": "Pop",
  "fileUrl": "/music/new-song.mp3",
  "coverImageUrl": "/images/new-album.jpg"
}
```

**Response:** `200 OK`

---

### Play Song
**POST** `/songs/{id}/play` 🔒

Record a song play and increment play count.

**Response:** `200 OK`

---

### Delete Song
**DELETE** `/songs/{id}` 🔒

Delete a song.

**Response:** `200 OK`

---

## Playlist Endpoints

### Get My Playlists
**GET** `/playlists/my` 🔒

Get all playlists for the authenticated user.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "My Favorites",
    "description": "My favorite songs",
    "coverImageUrl": "/images/playlist1.jpg",
    "songs": [
      {
        "id": 1,
        "title": "Hey Jude",
        "artistName": "The Beatles",
        "artistId": 1,
        "album": "The Beatles 1967-1970",
        "duration": 431,
        "genre": "Rock",
        "fileUrl": "/music/hey-jude.mp3",
        "coverImageUrl": "/images/beatles-1967-1970.jpg",
        "playCount": 15000
      }
    ],
    "isPublic": true
  }
]
```

---

### Get Public Playlists
**GET** `/playlists/public` 🔒

Get all public playlists.

**Response:** `200 OK`

---

### Get Playlist by ID
**GET** `/playlists/{id}` 🔒

Get details of a specific playlist.

**Response:** `200 OK`

---

### Create Playlist
**POST** `/playlists` 🔒

Create a new playlist.

**Request Body:**
```json
{
  "name": "My Rock Collection",
  "description": "Best rock songs",
  "coverImageUrl": "/images/rock-playlist.jpg",
  "isPublic": true
}
```

**Response:** `200 OK`

---

### Add Song to Playlist
**POST** `/playlists/{playlistId}/songs/{songId}` 🔒

Add a song to a playlist.

**Response:** `200 OK`

---

### Remove Song from Playlist
**DELETE** `/playlists/{playlistId}/songs/{songId}` 🔒

Remove a song from a playlist.

**Response:** `200 OK`

---

### Delete Playlist
**DELETE** `/playlists/{id}` 🔒

Delete a playlist.

**Response:** `200 OK`

---

## Artist Endpoints

### Get All Artists
**GET** `/artists` 🔒

Get all artists.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "The Beatles",
    "bio": "Legendary British rock band",
    "profileImage": null,
    "country": "United Kingdom",
    "followerCount": 150000,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

---

### Get Artist by ID
**GET** `/artists/{id}` 🔒

Get details of a specific artist.

**Response:** `200 OK`

---

### Search Artists
**GET** `/artists/search?keyword={keyword}`

Search for artists by name.

**Response:** `200 OK`

---

### Get Top Artists
**GET** `/artists/top`

Get the top 10 artists by follower count.

**Response:** `200 OK`

---

### Get Followed Artists
**GET** `/artists/following` 🔒

Get all artists followed by the authenticated user.

**Response:** `200 OK`

---

### Create Artist
**POST** `/artists` 🔒

Create a new artist.

**Request Body:**
```json
{
  "name": "New Artist",
  "bio": "Artist biography",
  "country": "United States",
  "profileImage": "/images/artist.jpg"
}
```

**Response:** `200 OK`

---

### Follow Artist
**POST** `/artists/{id}/follow` 🔒

Follow an artist.

**Response:** `200 OK`

---

### Unfollow Artist
**DELETE** `/artists/{id}/follow` 🔒

Unfollow an artist.

**Response:** `200 OK`

---

## Recommendation Endpoints

### Get Recommendations
**GET** `/recommendations` 🔒

Get personalized song recommendations based on listening history.

**Response:** `200 OK`
```json
[
  {
    "id": 5,
    "title": "Billie Jean",
    "artistName": "Michael Jackson",
    "artistId": 3,
    "album": "Thriller",
    "duration": 294,
    "genre": "Pop",
    "fileUrl": "/music/billie-jean.mp3",
    "coverImageUrl": "/images/thriller.jpg",
    "playCount": 30000
  }
]
```

---

## Error Responses

### 400 Bad Request
```json
{
  "message": "Validation error message"
}
```

### 401 Unauthorized
```json
{
  "message": "Unauthorized access"
}
```

### 404 Not Found
```json
{
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "message": "Internal server error"
}
```

---

## Notes

🔒 = Requires authentication

All timestamps are in ISO 8601 format.
All durations are in seconds.
