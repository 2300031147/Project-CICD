import api from './api';

const songService = {
  getAllSongs: () => api.get('/songs'),
  getSongById: (id) => api.get(`/songs/${id}`),
  searchSongs: (keyword) => api.get(`/songs/search?keyword=${keyword}`),
  getTopSongs: () => api.get('/songs/top'),
  getSongsByArtist: (artistId) => api.get(`/songs/artist/${artistId}`),
  getSongsByGenre: (genre) => api.get(`/songs/genre/${genre}`),
  playSong: (id) => api.post(`/songs/${id}/play`),
  createSong: (songData) => api.post('/songs', songData),
  deleteSong: (id) => api.delete(`/songs/${id}`),
};

export default songService;
