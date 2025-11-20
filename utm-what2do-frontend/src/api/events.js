import apiClient from './index';

export const eventsService = {
  async getEvents(params = {}) {
    const response = await apiClient.get('/events', { params });
    return response.data;
  },

  async getEventById(id) {
    const response = await apiClient.get(`/events/${id}`);
    return response.data;
  },

  async createEvent(eventData) {
    const response = await apiClient.post('/events', eventData);
    return response.data;
  },

  async updateEvent(id, eventData) {
    const response = await apiClient.put(`/events/${id}`, eventData);
    return response.data;
  },

  async deleteEvent(id) {
    const response = await apiClient.delete(`/events/${id}`);
    return response.data;
  },

  async likeEvent(id) {
    const response = await apiClient.post(`/events/${id}/like`);
    return response.data;
  },

  async unlikeEvent(id) {
    const response = await apiClient.delete(`/events/${id}/like`);
    return response.data;
  },

  async getEventsByBuilding(buildingId) {
    const response = await apiClient.get(`/buildings/${buildingId}/events`);
    return response.data;
  },

  async getEventsByClub(clubId) {
    const response = await apiClient.get(`/clubs/${clubId}/events`);
    return response.data;
  }
};

export default eventsService;
