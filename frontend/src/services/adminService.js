import api from './api';

const adminService = {
  // Get all songs
  getAllSongs: () => {
    return api.get('/admin/songs');
  },

  // Create a new song
  createSong: (songData) => {
    return api.post('/admin/songs', songData);
  },

  // Update an existing song
  updateSong: (id, songData) => {
    return api.put(`/admin/songs/${id}`, songData);
  },

  // Delete a song
  deleteSong: (id) => {
    return api.delete(`/admin/songs/${id}`);
  },

  // Scan music library
  scanLibrary: () => {
    return api.post('/admin/library/scan');
  },

  // Get library statistics
  getLibraryStats: () => {
    return api.get('/admin/library/stats');
  }
};

export default adminService;
