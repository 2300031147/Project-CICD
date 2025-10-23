import React from 'react';
import './Sidebar.css';

const Sidebar = ({ playlists, onViewChange, onLogout }) => {
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
