import apiClient from './index';

// Transform backend club response to frontend format
const transformClub = (club) => {
  if (!club) return null;
  return {
    ...club,
    membersCount: club.followersCount || 0,
    events: club.upcomingEvents || []
  };
};

export const clubsService = {
  async getClubs(params = {}) {
    const response = await apiClient.get('/clubs', { params });
    if (response.data?.data?.records) {
      response.data.data.records = response.data.data.records.map(transformClub);
    }
    return response.data;
  },

  // Fixed: Use /clubs/id/{id} instead of /clubs/{id}
  async getClubById(id) {
    const response = await apiClient.get(`/clubs/id/${id}`);
    if (response.data?.data) {
      response.data.data = transformClub(response.data.data);
    }
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
