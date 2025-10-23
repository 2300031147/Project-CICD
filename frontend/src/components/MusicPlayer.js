import React, { useState } from 'react';
import './MusicPlayer.css';

const MusicPlayer = ({ song }) => {
  const [isPlaying, setIsPlaying] = useState(false);

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="music-player">
      <div className="player-song-info">
        <div className="player-song-title">{song.title}</div>
        <div className="player-song-artist">{song.artistName}</div>
      </div>
      <div className="player-controls">
        <button className="control-button">⏮️</button>
        <button 
          className="control-button play-button"
          onClick={() => setIsPlaying(!isPlaying)}
        >
          {isPlaying ? '⏸️' : '▶️'}
        </button>
        <button className="control-button">⏭️</button>
      </div>
      <div className="player-progress">
        <span>0:00</span>
        <div className="progress-bar">
          <div className="progress-filled"></div>
        </div>
        <span>{formatDuration(song.duration)}</span>
      </div>
    </div>
  );
};

export default MusicPlayer;
