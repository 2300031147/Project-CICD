import api from './api';

const playlistService = {
  getMyPlaylists: () => api.get('/playlists/my'),
  getPublicPlaylists: () => api.get('/playlists/public'),
  getPlaylistById: (id) => api.get(`/playlists/${id}`),
  createPlaylist: (playlistData) => api.post('/playlists', playlistData),
  addSongToPlaylist: (playlistId, songId) => 
    api.post(`/playlists/${playlistId}/songs/${songId}`),
  removeSongFromPlaylist: (playlistId, songId) => 
    api.delete(`/playlists/${playlistId}/songs/${songId}`),
  deletePlaylist: (id) => api.delete(`/playlists/${id}`),
};

export default playlistService;
