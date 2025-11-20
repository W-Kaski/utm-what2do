import apiClient from './index';

export const authService = {
  async register(userData) {
    const response = await apiClient.post('/users/register', userData);
    return response.data;
  },

  async login(credentials) {
    const response = await apiClient.post('/users/login', credentials);
    if (response.data?.data?.token) {
      localStorage.setItem('auth_token', response.data.data.token);
      localStorage.setItem('user_info', JSON.stringify(response.data.data.user));
    }
    return response.data;
  },

  async logout() {
    try {
      await apiClient.post('/users/logout');
    } finally {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_info');
    }
  },

  async getCurrentUser() {
    const response = await apiClient.get('/users/me');
    return response.data;
  },

  async getUserById(id) {
    const response = await apiClient.get(`/users/${id}`);
    return response.data;
  },

  async updateProfile(profileData) {
    const response = await apiClient.put('/users/me', profileData);
    if (response.data?.data) {
      localStorage.setItem('user_info', JSON.stringify(response.data.data));
    }
    return response.data;
  },

  isAuthenticated() {
    return !!localStorage.getItem('auth_token');
  },

  getStoredUser() {
    const userStr = localStorage.getItem('user_info');
    return userStr ? JSON.parse(userStr) : null;
  }
};

export default authService;
