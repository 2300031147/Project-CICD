# Frontend Integration Guide for New Features

This guide shows how to integrate the new backend features into your React frontend.

## 1. Streaming Audio

### Update Song Service

Add to `frontend/src/services/songService.js`:

```javascript
// Get streaming URL for a song
export const getStreamUrl = (songId) => {
  return `${API_BASE_URL}/stream/${songId}`;
};
```

### Update Music Player Component

Modify `frontend/src/components/MusicPlayer.js`:

```javascript
import { getStreamUrl } from '../services/songService';

const MusicPlayer = ({ currentSong }) => {
  const audioRef = useRef(null);
  
  useEffect(() => {
    if (currentSong && audioRef.current) {
      // Use the streaming endpoint instead of direct file URL
      audioRef.current.src = getStreamUrl(currentSong.id);
      audioRef.current.play();
    }
  }, [currentSong]);

  return (
    <div className="music-player">
      <audio 
        ref={audioRef}
        controls
        onEnded={handleSongEnd}
        onTimeUpdate={handleTimeUpdate}
      />
      {/* Player UI components */}
    </div>
  );
};
```

---

## 2. Liked Songs Feature

### Create Liked Songs Service

Create `frontend/src/services/likedSongsService.js`:

```javascript
import api from './api';

// Like a song
export const likeSong = async (songId) => {
  const response = await api.post(`/liked-songs/${songId}`);
  return response.data;
};

// Unlike a song
export const unlikeSong = async (songId) => {
  const response = await api.delete(`/liked-songs/${songId}`);
  return response.data;
};

// Get all liked songs
export const getLikedSongs = async () => {
  const response = await api.get('/liked-songs');
  return response.data;
};

// Check if song is liked
export const isSongLiked = async (songId) => {
  const response = await api.get(`/liked-songs/${songId}/is-liked`);
  return response.data;
};
```

### Create Like Button Component

Create `frontend/src/components/LikeButton.js`:

```javascript
import React, { useState, useEffect } from 'react';
import { likeSong, unlikeSong, isSongLiked } from '../services/likedSongsService';
import { FaHeart, FaRegHeart } from 'react-icons/fa';

const LikeButton = ({ songId, onLikeChange }) => {
  const [liked, setLiked] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    checkIfLiked();
  }, [songId]);

  const checkIfLiked = async () => {
    try {
      const isLiked = await isSongLiked(songId);
      setLiked(isLiked);
    } catch (error) {
      console.error('Error checking like status:', error);
    }
  };

  const handleToggleLike = async () => {
    setLoading(true);
    try {
      if (liked) {
        await unlikeSong(songId);
        setLiked(false);
      } else {
        await likeSong(songId);
        setLiked(true);
      }
      if (onLikeChange) {
        onLikeChange(liked);
      }
    } catch (error) {
      console.error('Error toggling like:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <button 
      className={`like-button ${liked ? 'liked' : ''}`}
      onClick={handleToggleLike}
      disabled={loading}
      title={liked ? 'Unlike' : 'Like'}
    >
      {liked ? <FaHeart color="#1db954" /> : <FaRegHeart />}
    </button>
  );
};

export default LikeButton;
```

### Create Liked Songs Page

Create `frontend/src/pages/LikedSongs.js`:

```javascript
import React, { useState, useEffect } from 'react';
import { getLikedSongs } from '../services/likedSongsService';
import SongList from '../components/SongList';
import './LikedSongs.css';

const LikedSongs = () => {
  const [likedSongs, setLikedSongs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLikedSongs();
  }, []);

  const fetchLikedSongs = async () => {
    try {
      const songs = await getLikedSongs();
      setLikedSongs(songs);
    } catch (error) {
      console.error('Error fetching liked songs:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading your liked songs...</div>;
  }

  return (
    <div className="liked-songs-page">
      <div className="header">
        <h1>Liked Songs</h1>
        <p>{likedSongs.length} songs</p>
      </div>
      
      {likedSongs.length === 0 ? (
        <div className="empty-state">
          <p>You haven't liked any songs yet.</p>
          <p>Click the heart icon on songs you love!</p>
        </div>
      ) : (
        <SongList 
          songs={likedSongs} 
          showLikeButton={true}
          onSongRemoved={fetchLikedSongs}
        />
      )}
    </div>
  );
};

export default LikedSongs;
```

### Add CSS Styles

Create `frontend/src/pages/LikedSongs.css`:

```css
.liked-songs-page {
  padding: 20px;
}

.liked-songs-page .header {
  margin-bottom: 30px;
}

.liked-songs-page .header h1 {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 10px;
}

.liked-songs-page .header p {
  color: #b3b3b3;
  font-size: 14px;
}

.liked-songs-page .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #b3b3b3;
}

.liked-songs-page .empty-state p {
  margin: 10px 0;
  font-size: 16px;
}

.like-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.like-button:hover {
  transform: scale(1.1);
}

.like-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.like-button svg {
  width: 20px;
  height: 20px;
}
```

### Update Song List Component

Modify `frontend/src/components/SongList.js` to include the like button:

```javascript
import LikeButton from './LikeButton';

const SongList = ({ songs, showLikeButton = false, onSongRemoved }) => {
  return (
    <div className="song-list">
      {songs.map((song) => (
        <div key={song.id} className="song-item">
          <div className="song-info">
            <img src={song.coverImageUrl} alt={song.title} />
            <div className="song-details">
              <h3>{song.title}</h3>
              <p>{song.artistName}</p>
            </div>
          </div>
          
          <div className="song-actions">
            {showLikeButton && (
              <LikeButton 
                songId={song.id} 
                onLikeChange={onSongRemoved}
              />
            )}
            <span className="duration">{formatDuration(song.duration)}</span>
          </div>
        </div>
      ))}
    </div>
  );
};
```

### Update App Router

Add the route in `frontend/src/App.js`:

```javascript
import LikedSongs from './pages/LikedSongs';

function App() {
  return (
    <Router>
      <Routes>
        {/* Existing routes */}
        <Route path="/liked-songs" element={<LikedSongs />} />
      </Routes>
    </Router>
  );
}
```

### Update Sidebar Navigation

Add liked songs link to `frontend/src/components/Sidebar.js`:

```javascript
import { FaHeart } from 'react-icons/fa';

const Sidebar = () => {
  return (
    <div className="sidebar">
      {/* Existing navigation */}
      <nav>
        <Link to="/liked-songs" className="nav-item">
          <FaHeart />
          <span>Liked Songs</span>
        </Link>
      </nav>
    </div>
  );
};
```

---

## 3. Admin Features

### Update Admin Service

Add to `frontend/src/services/adminService.js`:

```javascript
// Check library stats (includes scan-on-startup status)
export const getLibraryStats = async () => {
  const response = await api.get('/admin/library/stats');
  return response.data;
};
```

### Display Library Information

Update the Admin component to show startup scan status:

```javascript
const Admin = () => {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const data = await getLibraryStats();
      setStats(data);
    } catch (error) {
      console.error('Error fetching stats:', error);
    }
  };

  return (
    <div className="admin-panel">
      <h1>Admin Dashboard</h1>
      
      {stats && (
        <div className="stats-grid">
          <div className="stat-card">
            <h3>Library Path</h3>
            <p>{stats.libraryPath}</p>
          </div>
          <div className="stat-card">
            <h3>Total Songs</h3>
            <p>{stats.totalSongs}</p>
          </div>
          <div className="stat-card">
            <h3>Total Artists</h3>
            <p>{stats.totalArtists}</p>
          </div>
          <div className="stat-card">
            <h3>Supported Formats</h3>
            <p>{stats.supportedFormats?.join(', ')}</p>
          </div>
        </div>
      )}
      
      {/* Existing admin features */}
    </div>
  );
};
```

---

## Complete Integration Checklist

- [ ] Update song streaming to use `/api/stream/{songId}` endpoint
- [ ] Test audio playback with seeking and resuming
- [ ] Implement liked songs service
- [ ] Create like button component
- [ ] Create liked songs page
- [ ] Add liked songs to sidebar navigation
- [ ] Update song list to show like buttons
- [ ] Test like/unlike functionality
- [ ] Test liked songs list retrieval
- [ ] Update admin dashboard with library stats
- [ ] Test with real audio files
- [ ] Verify JWT authentication on protected endpoints

---

## Testing Tips

1. **Streaming**: Use browser DevTools Network tab to verify Range requests
2. **Liked Songs**: Check that heart icon changes state immediately
3. **Authentication**: Ensure JWT token is included in all requests
4. **Error Handling**: Test with invalid song IDs and expired tokens
5. **Loading States**: Verify spinners appear during API calls

---

## Common Issues and Solutions

### Issue: Streaming doesn't work
**Solution**: Verify the backend `/api/stream/{songId}` endpoint is accessible and returns proper content-type headers.

### Issue: Like button doesn't update
**Solution**: Check that the JWT token is valid and included in the Authorization header.

### Issue: Liked songs list is empty
**Solution**: Ensure you're calling the endpoint with authentication and that you've actually liked some songs.

### Issue: CORS errors
**Solution**: Verify `cors.allowed-origins` in backend application.properties includes your frontend URL.

---

## Performance Optimization

1. **Debounce Like Button**: Prevent rapid like/unlike clicks
2. **Cache Like Status**: Store in local state to reduce API calls
3. **Lazy Load Liked Songs**: Implement pagination for large lists
4. **Preload Streaming**: Start buffering next song in queue

---

## Future Enhancements

Consider implementing:
- Bulk like/unlike operations
- Liked songs search and filtering
- Sort liked songs by date added, artist, or title
- Export liked songs as playlist
- Share liked songs with other users
