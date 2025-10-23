import React from 'react';
import './SongList.css';

const SongList = ({ songs, onSongSelect }) => {
  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="song-list">
      {songs.map((song, index) => (
        <div
          key={song.id}
          className="song-item"
          onClick={() => onSongSelect(song)}
        >
          <div className="song-number">{index + 1}</div>
          <div className="song-info">
            <div className="song-title">{song.title}</div>
            <div className="song-artist">{song.artistName}</div>
          </div>
          <div className="song-album">{song.album}</div>
          <div className="song-duration">{formatDuration(song.duration)}</div>
        </div>
      ))}
    </div>
  );
};

export default SongList;
