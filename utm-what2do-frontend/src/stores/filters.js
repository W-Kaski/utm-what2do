import { defineStore } from 'pinia';

export const useFilterStore = defineStore('filters', {
  state: () => ({
    searchTerm: '',
    selectedTags: [],
    sortBy: 'soonest',
    dateRange: {
      start: null,
      end: null
    }
  }),
  getters: {
    hasActiveFilters(state) {
      return Boolean(state.searchTerm || state.selectedTags.length || state.dateRange.start || state.dateRange.end);
    },
    activeFilterCount(state) {
      let count = 0;
      if (state.searchTerm) count += 1;
      count += state.selectedTags.length;
      if (state.dateRange.start || state.dateRange.end) count += 1;
      return count;
    }
  },
  actions: {
    setSearchTerm(term) {
      this.searchTerm = term;
    },
    setSortBy(sortKey) {
      this.sortBy = sortKey;
    },
    toggleTag(tag) {
      if (this.selectedTags.includes(tag)) {
        this.selectedTags = this.selectedTags.filter((t) => t !== tag);
      } else {
        this.selectedTags.push(tag);
      }
    },
    setDateRange(range) {
      this.dateRange = range;
    },
    clear() {
      this.searchTerm = '';
      this.selectedTags = [];
      this.sortBy = 'soonest';
      this.dateRange = { start: null, end: null };
    }
  }
});
