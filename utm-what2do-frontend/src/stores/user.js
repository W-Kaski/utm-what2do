import { defineStore } from 'pinia';
import { authService } from '@/api/auth';

const defaultCover =
  'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1600&q=80';
const defaultAvatar =
  'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80';

export const useUserStore = defineStore('user', {
  state: () => ({
    id: null,
    email: '',
    username: '',
    name: 'UTM Explorer',
    bio: 'Let inspiration spark anytime - explore campus and save the events you love',
    coverImage: defaultCover,
    avatar: defaultAvatar,
    favorites: [],
    isAuthenticated: false,
    loading: false,
    error: null
  }),
  actions: {
    async login(credentials) {
      this.loading = true;
      this.error = null;
      try {
        const response = await authService.login(credentials);
        if (response?.data?.user) {
          this.setUser(response.data.user);
        }
        return response;
      } catch (err) {
        this.error = err.response?.data?.message || 'Login failed';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async register(userData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await authService.register(userData);
        return response;
      } catch (err) {
        this.error = err.response?.data?.message || 'Registration failed';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    async logout() {
      try {
        await authService.logout();
      } finally {
        this.resetUser();
      }
    },

    async fetchCurrentUser() {
      this.loading = true;
      try {
        const response = await authService.getCurrentUser();
        if (response?.data) {
          this.setUser(response.data);
        }
      } catch (err) {
        console.error('Failed to fetch user:', err);
        // Try to load from localStorage
        const storedUser = authService.getStoredUser();
        if (storedUser) {
          this.setUser(storedUser);
        }
      } finally {
        this.loading = false;
      }
    },

    async updateProfile(profileData) {
      this.loading = true;
      this.error = null;
      try {
        const response = await authService.updateProfile(profileData);
        if (response?.data) {
          this.setUser(response.data);
        }
        return response;
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to update profile';
        throw err;
      } finally {
        this.loading = false;
      }
    },

    setUser(userData) {
      this.id = userData.id;
      this.email = userData.email || '';
      this.username = userData.username || '';
      this.name = userData.displayName || userData.name || 'UTM Explorer';
      this.bio = userData.bio || this.bio;
      this.avatar = userData.avatarUrl || userData.avatar || defaultAvatar;
      this.coverImage = userData.coverUrl || userData.coverImage || defaultCover;
      this.isAuthenticated = true;
    },

    resetUser() {
      this.id = null;
      this.email = '';
      this.username = '';
      this.name = 'UTM Explorer';
      this.bio = 'Let inspiration spark anytime - explore campus and save the events you love';
      this.coverImage = defaultCover;
      this.avatar = defaultAvatar;
      this.isAuthenticated = false;
    },

    initFromStorage() {
      const storedUser = authService.getStoredUser();
      if (storedUser) {
        this.setUser(storedUser);
      }
      this.isAuthenticated = authService.isAuthenticated();
    },

    toggleFavorite(eventId) {
      if (!eventId) return;
      if (this.favorites.includes(eventId)) {
        this.favorites = this.favorites.filter((id) => id !== eventId);
      } else {
        this.favorites.unshift(eventId);
      }
    },

    isFavorited(eventId) {
      return this.favorites.includes(eventId);
    },

    setCoverImage(dataUrl) {
      if (dataUrl) {
        this.coverImage = dataUrl;
      }
    },

    clearError() {
      this.error = null;
    }
  }
});
