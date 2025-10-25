import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import songService from '../services/songService';
import playlistService from '../services/playlistService';
import authService from '../services/authService';
import MusicPlayer from '../components/MusicPlayer';
import Sidebar from '../components/Sidebar';
import SearchBar from '../components/SearchBar';
import SongList from '../components/SongList';
import './Dashboard.css';

const Dashboard = () => {
  const [songs, setSongs] = useState([]);
  const [playlists, setPlaylists] = useState([]);
  const [currentSong, setCurrentSong] = useState(null);
  const [view, setView] = useState('home');
  const navigate = useNavigate();

  useEffect(() => {
    const user = authService.getCurrentUser();
    if (!user) {
      navigate('/login');
      return;
    }
    loadData();
  }, [navigate]);

  const loadData = async () => {
    try {
      const [songsRes, playlistsRes] = await Promise.all([
        songService.getTopSongs(),
        playlistService.getMyPlaylists(),
      ]);
      setSongs(songsRes.data);
      setPlaylists(playlistsRes.data);
    } catch (error) {
      console.error('Error loading data:', error);
    }
  };

  const handleSongSelect = async (song) => {
    setCurrentSong(song);
    try {
      await songService.playSong(song.id);
    } catch (error) {
      console.error('Error playing song:', error);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <div className="dashboard">
      <Sidebar 
        playlists={playlists} 
        onViewChange={setView}
        onLogout={handleLogout}
      />
      <div className="main-content">
        <SearchBar onSearch={(keyword) => {
          songService.searchSongs(keyword).then(res => setSongs(res.data));
        }} />
        <div className="content-area">
          {view === 'home' && (
            <>
              <h2>Top Songs</h2>
              <SongList songs={songs} onSongSelect={handleSongSelect} />
            </>
          )}
        </div>
      </div>
      {currentSong && <MusicPlayer song={currentSong} />}
    </div>
  );
};

export default Dashboard;
