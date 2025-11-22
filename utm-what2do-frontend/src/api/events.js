import apiClient from './index';

// Transform backend event response to frontend format
const transformEvent = (event) => {
  if (!event) return null;
  return {
    ...event,
    location: event.buildingName,
    locationId: event.buildingId,
    coverImage: event.coverImageUrl,
    organizer: {
      clubId: event.organizerId,
      name: event.organizerName,
      description: event.organizerBio,
      avatar: event.organizerLogo
    },
    registration: event.registrationUrl ? [{ url: event.registrationUrl }] : []
  };
};

export const eventsService = {
  async getEvents(params = {}) {
    const response = await apiClient.get('/events', { params });
    if (response.data?.data?.records) {
      response.data.data.records = response.data.data.records.map(transformEvent);
    }
    return response.data;
  },

  async getEventById(id) {
    const response = await apiClient.get(`/events/${id}`);
    if (response.data?.data) {
      response.data.data = transformEvent(response.data.data);
    }
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

  // Use interested endpoint for "liking" events
  async likeEvent(id) {
    const response = await apiClient.post(`/events/${id}/interested`);
    return response.data;
  },

  async unlikeEvent(id) {
    const response = await apiClient.delete(`/events/${id}/interested`);
    return response.data;
  },

  async getEventsByBuilding(buildingId) {
    const response = await apiClient.get(`/buildings/${buildingId}/events`);
    if (response.data?.data?.records) {
      response.data.data.records = response.data.data.records.map(transformEvent);
    }
    return response.data;
  },

  async getEventsByClub(clubId) {
    const response = await apiClient.get(`/clubs/${clubId}/events`);
    if (response.data?.data?.records) {
      response.data.data.records = response.data.data.records.map(transformEvent);
    }
    return response.data;
  }
};

export default eventsService;
