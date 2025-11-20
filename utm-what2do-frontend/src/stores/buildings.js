import { defineStore } from 'pinia';
import { buildingsService } from '@/api/buildings';
import { campusBuildings } from '@/data/campusMap';

export const useBuildingStore = defineStore('buildings', {
  state: () => ({
    buildings: [],
    selectedBuilding: null,
    buildingEvents: [],
    loading: false,
    error: null
  }),
  getters: {
    allBuildings: (state) => state.buildings,
    getBuildingById: (state) => (id) => state.buildings.find((b) => b.id === id),
    getBuildingsByCategory: (state) => (category) => {
      if (!category) return state.buildings;
      return state.buildings.filter(b => b.category === category);
    }
  },
  actions: {
    async fetchBuildings(params = {}) {
      this.loading = true;
      this.error = null;
      try {
        const response = await buildingsService.getBuildings(params);
        if (response?.data) {
          this.buildings = response.data.records || response.data;
        }
      } catch (err) {
        console.error('Failed to fetch buildings:', err);
        this.error = err.message || 'Failed to fetch buildings';
        // Fallback to mock data
        this.buildings = campusBuildings;
      } finally {
        this.loading = false;
      }
    },

    async fetchBuildingEvents(buildingId) {
      this.loading = true;
      this.error = null;
      try {
        const response = await buildingsService.getBuildingEvents(buildingId);
        if (response?.data) {
          this.buildingEvents = response.data.records || response.data;
          return this.buildingEvents;
        }
      } catch (err) {
        console.error('Failed to fetch building events:', err);
        this.error = err.message || 'Failed to fetch building events';
        this.buildingEvents = [];
      } finally {
        this.loading = false;
      }
      return this.buildingEvents;
    },

    selectBuilding(id) {
      this.selectedBuilding = this.buildings.find((b) => b.id === id) || null;
    },

    clearError() {
      this.error = null;
    }
  }
});
