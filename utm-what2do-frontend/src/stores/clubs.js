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
      const club = this.clubs.find(c => c.id === clubId);
      if (!club) return;

      // Save original state for rollback
      const originalCount = club.membersCount || 0;
      const originalJoined = club.isJoined;

      // Optimistic update
      club.membersCount = originalCount + 1;
      club.isJoined = true;

      try {
        await clubsService.joinClub(clubId);
      } catch (err) {
        console.error('Failed to join club:', err);
        // Rollback on error
        club.membersCount = originalCount;
        club.isJoined = originalJoined;
        throw err;
      }
    },

    async leaveClub(clubId) {
      const club = this.clubs.find(c => c.id === clubId);
      if (!club) return;

      // Save original state for rollback
      const originalCount = club.membersCount || 0;
      const originalJoined = club.isJoined;

      // Optimistic update
      club.membersCount = Math.max(originalCount - 1, 0);
      club.isJoined = false;

      try {
        await clubsService.leaveClub(clubId);
      } catch (err) {
        console.error('Failed to leave club:', err);
        // Rollback on error
        club.membersCount = originalCount;
        club.isJoined = originalJoined;
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
