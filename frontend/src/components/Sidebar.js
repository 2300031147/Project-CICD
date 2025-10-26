import React from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import './Sidebar.css';

const Sidebar = ({ playlists, onViewChange, onLogout }) => {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();
  const isAdmin = user && user.role === 'ADMIN';

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h1>🎵 Music</h1>
      </div>
      <nav className="sidebar-nav">
        <button onClick={() => onViewChange('home')} className="nav-item">
          🏠 Home
        </button>
        <button onClick={() => onViewChange('search')} className="nav-item">
          🔍 Search
        </button>
        <button onClick={() => onViewChange('library')} className="nav-item">
          📚 Your Library
        </button>
        {isAdmin && (
          <button onClick={() => navigate('/admin')} className="nav-item admin-link">
            ⚙️ Admin Panel
          </button>
        )}
      </nav>
      <div className="sidebar-section">
        <h3>Playlists</h3>
        <button className="create-playlist">+ Create Playlist</button>
        <div className="playlist-list">
          {playlists.map((playlist) => (
            <div key={playlist.id} className="playlist-item">
              {playlist.name}
            </div>
          ))}
        </div>
      </div>
      <div className="sidebar-footer">
        <button onClick={onLogout} className="logout-button">
          Logout
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
