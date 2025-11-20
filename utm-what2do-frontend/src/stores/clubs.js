import { defineStore } from 'pinia';
import { clubsService } from '@/api/clubs';
import { clubs as mockClubs } from '@/data/clubs';

export const useClubStore = defineStore('clubs', {
  state: () => ({
    clubs: [],
    selectedClub: null,
    loading: false,
    error: null,
    pagination: {
      page: 1,
      pageSize: 10,
      total: 0
    }
  }),
  getters: {
    allClubs: (state) => state.clubs,
    getClubById: (state) => (id) => state.clubs.find((club) => club.id === id),
    getClubsByCategory: (state) => (category) => {
      if (!category || category === 'All') return state.clubs;
      return state.clubs.filter(club => club.category === category);
    }
  },
  actions: {
    async fetchClubs(params = {}) {
      this.loading = true;
      this.error = null;
      try {
        const response = await clubsService.getClubs(params);
        if (response?.data) {
          this.clubs = response.data.records || response.data;
          if (response.data.total) {
            this.pagination.total = response.data.total;
          }
        }
      } catch (err) {
        console.error('Failed to fetch clubs:', err);
        this.error = err.message || 'Failed to fetch clubs';
        // Fallback to mock data
        this.clubs = mockClubs;
      } finally {
        this.loading = false;
      }
    },

    async fetchClubById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await clubsService.getClubById(id);
        if (response?.data) {
          this.selectedClub = response.data;
          return response.data;
        }
      } catch (err) {
        console.error('Failed to fetch club:', err);
        this.error = err.message || 'Failed to fetch club';
        // Fallback to mock data
        this.selectedClub = mockClubs.find(c => c.id === id) || null;
      } finally {
        this.loading = false;
      }
      return this.selectedClub;
    },

    async joinClub(clubId) {
      try {
        await clubsService.joinClub(clubId);
        const club = this.clubs.find(c => c.id === clubId);
        if (club) {
          club.membersCount = (club.membersCount || 0) + 1;
          club.isJoined = true;
        }
      } catch (err) {
        console.error('Failed to join club:', err);
        throw err;
      }
    },

    async leaveClub(clubId) {
      try {
        await clubsService.leaveClub(clubId);
        const club = this.clubs.find(c => c.id === clubId);
        if (club) {
          club.membersCount = Math.max((club.membersCount || 1) - 1, 0);
          club.isJoined = false;
        }
      } catch (err) {
        console.error('Failed to leave club:', err);
        throw err;
      }
    },

    selectClub(id) {
      this.selectedClub = this.clubs.find((club) => club.id === id) || null;
    },

    clearError() {
      this.error = null;
    }
  }
});
