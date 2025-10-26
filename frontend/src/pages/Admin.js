import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import adminService from '../services/adminService';
import artistService from '../services/artistService';
import authService from '../services/authService';
import './Admin.css';

const Admin = () => {
  const [songs, setSongs] = useState([]);
  const [artists, setArtists] = useState([]);
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingSong, setEditingSong] = useState(null);
  const [scanResult, setScanResult] = useState(null);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: '',
    artistId: '',
    album: '',
    duration: '',
    genre: '',
    fileUrl: '',
    coverImageUrl: ''
  });

  useEffect(() => {
    const user = authService.getCurrentUser();
    if (!user) {
      navigate('/login');
      return;
    }
    // Check if user is admin (you might want to add a role check here)
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [navigate]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [songsRes, artistsRes, statsRes] = await Promise.all([
        adminService.getAllSongs(),
        artistService.getAllArtists(),
        adminService.getLibraryStats()
      ]);
      setSongs(songsRes.data);
      setArtists(artistsRes.data);
      setStats(statsRes.data);
    } catch (error) {
      console.error('Error loading data:', error);
      if (error.response && error.response.status === 403) {
        alert('Access denied. Admin privileges required.');
        navigate('/dashboard');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleScanLibrary = async () => {
    try {
      setLoading(true);
      setScanResult(null);
      const response = await adminService.scanLibrary();
      setScanResult(response.data);
      loadData(); // Reload data after scan
    } catch (error) {
      console.error('Error scanning library:', error);
      alert('Failed to scan library: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const songData = {
        ...formData,
        duration: parseInt(formData.duration),
        artistId: parseInt(formData.artistId)
      };

      if (editingSong) {
        await adminService.updateSong(editingSong.id, songData);
        alert('Song updated successfully!');
      } else {
        await adminService.createSong(songData);
        alert('Song added successfully!');
      }
      
      setShowAddModal(false);
      setEditingSong(null);
      setFormData({
        title: '',
        artistId: '',
        album: '',
        duration: '',
        genre: '',
        fileUrl: '',
        coverImageUrl: ''
      });
      loadData();
    } catch (error) {
      console.error('Error saving song:', error);
      alert('Failed to save song: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (song) => {
    setEditingSong(song);
    setFormData({
      title: song.title,
      artistId: song.artistId,
      album: song.album,
      duration: song.duration,
      genre: song.genre,
      fileUrl: song.fileUrl,
      coverImageUrl: song.coverImageUrl || ''
    });
    setShowAddModal(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this song?')) {
      return;
    }
    try {
      setLoading(true);
      await adminService.deleteSong(id);
      alert('Song deleted successfully!');
      loadData();
    } catch (error) {
      console.error('Error deleting song:', error);
      alert('Failed to delete song: ' + (error.response?.data?.message || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Admin Dashboard</h1>
        <div className="admin-actions">
          <button onClick={() => navigate('/dashboard')} className="btn-secondary">
            Back to Dashboard
          </button>
          <button onClick={handleLogout} className="btn-secondary">
            Logout
          </button>
        </div>
      </div>

      <div className="admin-stats">
        <div className="stat-card">
          <h3>Total Songs</h3>
          <p className="stat-value">{stats.totalSongs || 0}</p>
        </div>
        <div className="stat-card">
          <h3>Total Artists</h3>
          <p className="stat-value">{stats.totalArtists || 0}</p>
        </div>
        <div className="stat-card">
          <h3>Library Path</h3>
          <p className="stat-text">{stats.libraryPath || 'Not configured'}</p>
        </div>
      </div>

      <div className="admin-library-section">
        <h2>Music Library</h2>
        <div className="library-controls">
          <button 
            onClick={handleScanLibrary} 
            className="btn-primary"
            disabled={loading}
          >
            {loading ? 'Scanning...' : 'Scan Library'}
          </button>
          <button 
            onClick={() => {
              setEditingSong(null);
              setFormData({
                title: '',
                artistId: '',
                album: '',
                duration: '',
                genre: '',
                fileUrl: '',
                coverImageUrl: ''
              });
              setShowAddModal(true);
            }} 
            className="btn-primary"
          >
            Add Song Manually
          </button>
        </div>
        
        {scanResult && (
          <div className={`scan-result ${scanResult.status === 'success' ? 'success' : 'error'}`}>
            <h3>Scan Result</h3>
            {scanResult.status === 'success' ? (
              <div>
                <p>Scanned Files: {scanResult.scannedFiles}</p>
                <p>Imported Songs: {scanResult.importedSongs}</p>
                <p>Skipped Files: {scanResult.skippedFiles}</p>
                {scanResult.errors && scanResult.errors.length > 0 && (
                  <div>
                    <h4>Errors:</h4>
                    <ul>
                      {scanResult.errors.map((error, idx) => (
                        <li key={idx}>{error}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            ) : (
              <p>Error: {scanResult.message}</p>
            )}
          </div>
        )}
        
        <div className="supported-formats">
          <h4>Supported Formats:</h4>
          <p>{stats.supportedFormats?.join(', ') || 'MP3, WAV, FLAC, AAC, M4A, AIFF, AIF, ALAC, OGG, WMA'}</p>
        </div>
      </div>

      <div className="admin-songs-section">
        <h2>Manage Songs</h2>
        <div className="songs-table-container">
          <table className="songs-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Artist</th>
                <th>Album</th>
                <th>Genre</th>
                <th>Duration</th>
                <th>Play Count</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {songs.map(song => (
                <tr key={song.id}>
                  <td>{song.id}</td>
                  <td>{song.title}</td>
                  <td>{song.artistName}</td>
                  <td>{song.album}</td>
                  <td>{song.genre}</td>
                  <td>{Math.floor(song.duration / 60)}:{(song.duration % 60).toString().padStart(2, '0')}</td>
                  <td>{song.playCount}</td>
                  <td>
                    <button onClick={() => handleEdit(song)} className="btn-edit">Edit</button>
                    <button onClick={() => handleDelete(song.id)} className="btn-delete">Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>{editingSong ? 'Edit Song' : 'Add New Song'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Title:</label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Artist:</label>
                <select
                  name="artistId"
                  value={formData.artistId}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Select Artist</option>
                  {artists.map(artist => (
                    <option key={artist.id} value={artist.id}>{artist.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Album:</label>
                <input
                  type="text"
                  name="album"
                  value={formData.album}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Duration (seconds):</label>
                <input
                  type="number"
                  name="duration"
                  value={formData.duration}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Genre:</label>
                <input
                  type="text"
                  name="genre"
                  value={formData.genre}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>File URL:</label>
                <input
                  type="text"
                  name="fileUrl"
                  value={formData.fileUrl}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Cover Image URL:</label>
                <input
                  type="text"
                  name="coverImageUrl"
                  value={formData.coverImageUrl}
                  onChange={handleInputChange}
                />
              </div>
              <div className="form-actions">
                <button type="submit" className="btn-primary" disabled={loading}>
                  {loading ? 'Saving...' : (editingSong ? 'Update' : 'Add')}
                </button>
                <button 
                  type="button" 
                  onClick={() => {
                    setShowAddModal(false);
                    setEditingSong(null);
                  }} 
                  className="btn-secondary"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Admin;
