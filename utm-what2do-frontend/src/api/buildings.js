import apiClient from './index';

export const buildingsService = {
  async getBuildings(params = {}) {
    const response = await apiClient.get('/buildings', { params });
    return response.data;
  },

  async getBuildingById(id) {
    const response = await apiClient.get(`/buildings/${id}`);
    return response.data;
  },

  async getBuildingEvents(buildingId) {
    const response = await apiClient.get(`/buildings/${buildingId}/events`);
    return response.data;
  }
};

export default buildingsService;
