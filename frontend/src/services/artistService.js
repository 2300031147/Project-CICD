import api from './api';

const artistService = {
  getAllArtists: () => api.get('/artists'),
  getArtistById: (id) => api.get(`/artists/${id}`),
  searchArtists: (keyword) => api.get(`/artists/search?keyword=${keyword}`),
  getTopArtists: () => api.get('/artists/top'),
  getFollowedArtists: () => api.get('/artists/following'),
  followArtist: (id) => api.post(`/artists/${id}/follow`),
  unfollowArtist: (id) => api.delete(`/artists/${id}/follow`),
  createArtist: (artistData) => api.post('/artists', artistData),
};

export default artistService;
