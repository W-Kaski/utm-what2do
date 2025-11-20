import apiClient from './index';

export const clubsService = {
  async getClubs(params = {}) {
    const response = await apiClient.get('/clubs', { params });
    return response.data;
  },

  async getClubById(id) {
    const response = await apiClient.get(`/clubs/${id}`);
    return response.data;
  },

  async createClub(clubData) {
    const response = await apiClient.post('/clubs', clubData);
    return response.data;
  },

  async updateClub(id, clubData) {
    const response = await apiClient.put(`/clubs/${id}`, clubData);
    return response.data;
  },

  async deleteClub(id) {
    const response = await apiClient.delete(`/clubs/${id}`);
    return response.data;
  },

  async getClubMembers(clubId) {
    const response = await apiClient.get(`/clubs/${clubId}/members`);
    return response.data;
  },

  async joinClub(clubId) {
    const response = await apiClient.post(`/clubs/${clubId}/join`);
    return response.data;
  },

  async leaveClub(clubId) {
    const response = await apiClient.delete(`/clubs/${clubId}/leave`);
    return response.data;
  },

  async getClubPosts(clubId) {
    const response = await apiClient.get(`/clubs/${clubId}/posts`);
    return response.data;
  },

  async getClubEvents(clubId) {
    const response = await apiClient.get(`/clubs/${clubId}/events`);
    return response.data;
  }
};

export default clubsService;
