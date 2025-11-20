import apiClient from './index';

export const followsService = {
  async followUser(userId) {
    const response = await apiClient.post(`/follows/${userId}`);
    return response.data;
  },

  async unfollowUser(userId) {
    const response = await apiClient.delete(`/follows/${userId}`);
    return response.data;
  },

  async getFollowers(userId) {
    const response = await apiClient.get(`/users/${userId}/followers`);
    return response.data;
  },

  async getFollowing(userId) {
    const response = await apiClient.get(`/users/${userId}/following`);
    return response.data;
  },

  async checkFollowing(userId) {
    const response = await apiClient.get(`/follows/${userId}/check`);
    return response.data;
  }
};

export default followsService;
