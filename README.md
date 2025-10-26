# Music Streaming Platform 🎵

A full-featured music streaming platform similar to Spotify, built with **React.js** frontend and **Spring Boot** backend, using **MySQL** database.

## Features

### 🎧 Core Features
- **Music Streaming**: Play, pause, and control music playback
- **Search**: Find songs, artists, and albums
- **Download**: Download songs for offline listening
- **Playlists**: Create and manage custom playlists
- **Artist Following**: Follow your favorite artists
- **AI-Based Recommendations**: Get personalized song recommendations based on listening history
- **Analytics**: Track play history and user engagement

### ⚙️ Admin Features
- **Admin Dashboard**: Comprehensive admin panel for managing the music library
- **Song Management**: Add, edit, and delete songs manually
- **Automatic Library Scanning**: Scan and import songs from a configured folder automatically
- **Multi-Format Support**: Supports MP3, WAV, FLAC, AAC, M4A, AIFF, AIF, ALAC, OGG, WMA
- **Metadata Extraction**: Automatically extracts title, artist, album, genre, and duration from audio files
- **Library Statistics**: View total songs, artists, and library configuration

### 🔐 Authentication & Security
- **JWT Authentication**: Secure token-based authentication
- **OAuth Support**: OAuth2 resource server integration
- **Password Encryption**: BCrypt password hashing
- **Role-Based Access Control**: User, Artist, and Admin roles

### 🎨 User Interface
- **Spotify-like Design**: Modern, responsive UI inspired by Spotify
- **Music Player**: Bottom-fixed player with playback controls
- **Search Bar**: Real-time search functionality
- **Sidebar Navigation**: Easy navigation through playlists and library

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA with Hibernate

### Frontend
- **Framework**: React 18.2.0
- **Routing**: React Router DOM
- **HTTP Client**: Axios
- **Build Tool**: React Scripts

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **CI/CD**: GitHub Actions
- **Web Server**: Nginx (for frontend)

## Prerequisites

- Docker and Docker Compose
- Java 17 (for local development)
- Node.js 18+ (for local development)
- MySQL 8.0 (for local development)

## Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone https://github.com/2300031147/Project-CICD.git
   cd Project-CICD
   ```

2. **Start the application**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - MySQL: localhost:3306

4. **Stop the application**
   ```bash
   docker-compose down
   ```

## Admin Panel Access

To access the admin panel:

1. **Create an admin user**: You need to create a user with ADMIN role in the database. You can do this by:
   - Registering a normal user through the UI
   - Manually updating the user's role in the database:
     ```sql
     UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
     ```

2. **Login with admin credentials** at http://localhost:3000/login

3. **Access admin panel**: After logging in, you'll see an "Admin Panel" option in the sidebar (only visible to admin users)

4. **Admin Features**:
   - View all songs in the library
   - Add songs manually with metadata
   - Edit existing songs
   - Delete songs
   - Scan and import songs from configured music folder
   - View library statistics

## Local Development Setup

### Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Configure MySQL**
   - Username: `root`
   - Password: `1108`
   - Database: `musicdb`

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or build and run:
   ```bash
   mvn clean package
   java -jar target/streaming-1.0.0.jar
   ```

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```

   The application will open at http://localhost:3000

## API Documentation

### Authentication Endpoints

- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Song Endpoints

- `GET /api/songs` - Get all songs
- `GET /api/songs/{id}` - Get song by ID
- `GET /api/songs/search?keyword=` - Search songs
- `GET /api/songs/top` - Get top songs
- `POST /api/songs` - Create new song
- `POST /api/songs/{id}/play` - Play song (records analytics)
- `DELETE /api/songs/{id}` - Delete song

### Playlist Endpoints

- `GET /api/playlists/my` - Get user's playlists
- `GET /api/playlists/public` - Get public playlists
- `GET /api/playlists/{id}` - Get playlist by ID
- `POST /api/playlists` - Create playlist
- `POST /api/playlists/{playlistId}/songs/{songId}` - Add song to playlist
- `DELETE /api/playlists/{playlistId}/songs/{songId}` - Remove song from playlist
- `DELETE /api/playlists/{id}` - Delete playlist

### Artist Endpoints

- `GET /api/artists` - Get all artists
- `GET /api/artists/{id}` - Get artist by ID
- `GET /api/artists/search?keyword=` - Search artists
- `GET /api/artists/top` - Get top artists
- `GET /api/artists/following` - Get followed artists
- `POST /api/artists/{id}/follow` - Follow artist
- `DELETE /api/artists/{id}/follow` - Unfollow artist

### Recommendation Endpoints

- `GET /api/recommendations` - Get personalized recommendations

### Admin Endpoints (Require ADMIN role)

- `GET /api/admin/songs` - Get all songs (admin view)
- `POST /api/admin/songs` - Create new song
- `PUT /api/admin/songs/{id}` - Update existing song
- `DELETE /api/admin/songs/{id}` - Delete song
- `POST /api/admin/library/scan` - Scan and import songs from configured library path
- `GET /api/admin/library/stats` - Get library statistics

## Database Schema

### Users Table
- id, username, email, password, firstName, lastName, profileImage, role, enabled, createdAt, updatedAt

### Songs Table
- id, title, artistId, album, duration, genre, fileUrl, coverImageUrl, playCount, createdAt, updatedAt, releasedAt

### Artists Table
- id, name, bio, profileImage, country, followerCount, createdAt, updatedAt

### Playlists Table
- id, name, description, coverImageUrl, isPublic, createdAt, updatedAt

### PlayHistory Table
- id, userId, songId, playedAt, duration

## Configuration

### Backend Configuration (`application.properties`)
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/musicdb
spring.datasource.username=root
spring.datasource.password=1108

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# File Upload
spring.servlet.multipart.max-file-size=50MB

# Music Library (for automatic scanning)
music.library.path=/path/to/your/music/folder
music.library.scan-on-startup=false
```

**Note for Windows users**: Use forward slashes or double backslashes for the path, e.g.:
- `music.library.path=C:/Users/YourName/Music/Project music`
- Or with environment variable: `MUSIC_LIBRARY_PATH=C:/Users/YourName/Music/Project music`

### Frontend Environment Variables
```bash
REACT_APP_API_URL=http://localhost:8080/api
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow that:
1. Builds and tests the backend (Maven)
2. Builds and tests the frontend (npm)
3. Builds Docker images
4. Tests Docker Compose configuration

## Security Features

- JWT-based authentication
- Password encryption with BCrypt
- CORS configuration
- Role-based access control
- Secure API endpoints

## Future Enhancements

- [ ] Social sharing features
- [ ] Live streaming support
- [ ] Mobile applications (iOS/Android)
- [ ] Advanced analytics dashboard
- [ ] Lyrics integration
- [ ] Collaborative playlists
- [ ] Music upload by artists
- [ ] Payment integration for premium features

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Authors

- **Project Lead** - [2300031147](https://github.com/2300031147)
- **Project Team** - [2300030593](https://github.com/banny1708)

## Acknowledgments

- Spotify for UI/UX inspiration
- Spring Boot community
- React community