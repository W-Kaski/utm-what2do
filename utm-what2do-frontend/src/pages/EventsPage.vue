<template>
  <div class="events-page">
    <section class="search-section">
      <form class="search-form" @submit.prevent="submitSearch">
        <label class="sr-only" for="events-search">Search events</label>
        <input
          id="events-search"
          v-model="localSearch"
          type="search"
          placeholder="Enter keywords (for example: concert, workshop, tech)"
        />
        <button type="submit">Search</button>
      </form>
      <div class="search-actions">
        <button type="button" class="ghost-btn" @click="toggleFilters">
          Filter
          <span v-if="filterCount">({{ filterCount }})</span>
        </button>
        <button type="button" class="ghost-btn" @click="toggleSort">
          Sort · {{ sortLabel }}
        </button>
      </div>
    </section>

    <section v-if="showFilters" class="panel filter-panel">
      <header>
        <h3>Tag filters</h3>
        <button type="button" @click="clearFilters">Clear</button>
      </header>
      <div class="tag-list">
        <button
          v-for="tag in availableTags"
          :key="tag"
          type="button"
          :class="{ active: selectedTags.includes(tag) }"
          @click="toggleTag(tag)"
        >
          {{ tag }}
        </button>
      </div>
    </section>

    <section v-if="showSort" class="panel sort-panel">
      <h3>Sort order</h3>
      <ul>
        <li v-for="option in sortOptions" :key="option.value">
          <label>
            <input
              type="radio"
              :value="option.value"
              v-model="sortSelection"
              @change="selectSort(option.value)"
            />
            <span>{{ option.label }}</span>
          </label>
        </li>
      </ul>
    </section>

    <div class="result-headline">
      <div>
        <p class="eyebrow">Search results</p>
        <h2>
          <span v-if="searchTerm">"{{ searchTerm }}" · </span>
          Total {{ filteredEvents.length }} events
        </h2>
      </div>
      <button v-if="hasActiveFilters" type="button" class="clear-btn" @click="resetAll">
        Clear filters
      </button>
    </div>

    <section v-if="filteredEvents.length" class="grid">
      <EventCard v-for="event in filteredEvents" :key="event.id" :event="event" />
    </section>

    <div v-else class="empty-state">
      <h3>No events match yet</h3>
      <p>Try changing keywords or filters, or check back for new activities soon.</p>
      <button type="button" @click="resetAll">Reset search</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useRoute, useRouter } from 'vue-router';

import EventCard from '@/components/EventCard.vue';
import { useEventStore } from '@/stores/events';
import { useFilterStore } from '@/stores/filters';

import { HOT_EVENT_THRESHOLD, HOT_EVENT_TAG, OFFICIAL_TAG } from '@/constants/highlights';

const availableTags = ['Tech', 'Social', 'Competition', 'Music', HOT_EVENT_TAG, OFFICIAL_TAG];
const sortOptions = [
  { value: 'soonest', label: 'Soonest' },
  { value: 'latest', label: 'Latest' },
  { value: 'name', label: 'Name A-Z' }
];

const eventStore = useEventStore();
const filterStore = useFilterStore();
const route = useRoute();
const router = useRouter();

const { searchTerm, selectedTags, sortBy } = storeToRefs(filterStore);

const localSearch = ref(searchTerm.value);
const showFilters = ref(false);
const showSort = ref(false);
const sortSelection = ref(sortBy.value);

const filterCount = computed(() => selectedTags.value.length);
const hasActiveFilters = computed(() => filterStore.hasActiveFilters);
const sortLabel = computed(() => {
  const current = sortOptions.find((option) => option.value === sortBy.value);
  return current ? current.label : 'Soonest';
});

watch(searchTerm, (value) => {
  localSearch.value = value;
});

watch(sortBy, (value) => {
  sortSelection.value = value;
});

const normalizeTagFromQuery = (value) => {
  if (!value) return '';
  if (value === 'official') return OFFICIAL_TAG;
  return value;
};

const syncFromQuery = () => {
  const queryValue = typeof route.query.search === 'string' ? route.query.search : '';
  if (queryValue !== searchTerm.value) {
    filterStore.setSearchTerm(queryValue);
  }
  const tagValue = normalizeTagFromQuery(typeof route.query.tag === 'string' ? route.query.tag : '');
  if (tagValue) {
    selectedTags.value = [tagValue];
  }
};

onMounted(syncFromQuery);
watch(
  () => [route.query.search, route.query.tag],
  () => {
    syncFromQuery();
  }
);

const getTimeValue = (value, fallback) => {
  const time = value ? new Date(value).getTime() : NaN;
  return Number.isNaN(time) ? fallback : time;
};

const filteredEvents = computed(() => {
  const term = searchTerm.value.trim().toLowerCase();
  const list = eventStore.events.filter((event) => {
    const titleMatch = event.title.toLowerCase().includes(term);
    const descMatch = event.description.toLowerCase().includes(term);
    const matchesSearch = !term || titleMatch || descMatch;
    const matchesTag =
      !selectedTags.value.length ||
      selectedTags.value.some((tag) => {
        if (tag === OFFICIAL_TAG) return Boolean(event.isOfficial);
        if (tag === HOT_EVENT_TAG) return (event.signupCount || 0) >= HOT_EVENT_THRESHOLD;
        return event.tags?.includes(tag);
      });
    return matchesSearch && matchesTag;
  });

  const sorted = [...list];
  if (sortBy.value === 'latest') {
    sorted.sort((a, b) => getTimeValue(b.date, -Infinity) - getTimeValue(a.date, -Infinity));
  } else if (sortBy.value === 'name') {
    sorted.sort((a, b) => a.title.localeCompare(b.title, 'en'));
  } else {
    sorted.sort((a, b) => getTimeValue(a.date, Infinity) - getTimeValue(b.date, Infinity));
  }
  return sorted;
});

const submitSearch = () => {
  const value = localSearch.value.trim();
  filterStore.setSearchTerm(value);
  const nextQuery = { ...route.query };
  if (value) {
    nextQuery.search = value;
  } else {
    delete nextQuery.search;
  }
  router
    .replace({
      name: route.name || 'events',
      query: nextQuery
    })
    .catch(() => {});
};

const toggleFilters = () => {
  showFilters.value = !showFilters.value;
  if (showFilters.value) showSort.value = false;
};

const toggleSort = () => {
  showSort.value = !showSort.value;
  if (showSort.value) showFilters.value = false;
};

const updateTagQuery = () => {
  const specialTag = selectedTags.value.find((tag) => tag === OFFICIAL_TAG || tag === HOT_EVENT_TAG);
  const query = { ...route.query };
  if (specialTag === OFFICIAL_TAG) {
    query.tag = 'official';
  } else if (specialTag === HOT_EVENT_TAG) {
    query.tag = HOT_EVENT_TAG;
  } else {
    delete query.tag;
  }
  router.replace({ name: route.name || 'events', query }).catch(() => {});
};

const toggleTag = (tag) => {
  filterStore.toggleTag(tag);
  updateTagQuery();
};

const clearFilters = () => {
  selectedTags.value = [];
  updateTagQuery();
};

const selectSort = (value) => {
  filterStore.setSortBy(value);
  showSort.value = false;
};

const resetAll = () => {
  filterStore.clear();
  localSearch.value = '';
  showFilters.value = false;
  showSort.value = false;
  router
    .replace({ name: 'events', query: {} })
    .catch(() => {});
};
</script>

<style scoped>
.events-page {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.search-section {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: flex-start;
}

.search-form {
  flex: 1;
  display: flex;
  background: #fff;
  padding: 0.5rem;
  border-radius: 999px;
  box-shadow: 0 15px 35px rgba(15, 23, 42, 0.07);
}

.search-form input {
  flex: 1;
  border: none;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border-radius: 999px;
  outline: none;
}

.search-form button {
  border: none;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  padding: 0.5rem 1.25rem;
  font-weight: 600;
}

.search-actions {
  display: flex;
  gap: 0.75rem;
}

.ghost-btn {
  border: 1px solid rgba(148, 163, 184, 0.5);
  background: #fff;
  border-radius: 999px;
  padding: 0.45rem 1.1rem;
  font-weight: 600;
  color: #475569;
}

.panel {
  background: #fff;
  border-radius: 1.25rem;
  padding: 1.2rem;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
}

.filter-panel header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.filter-panel header button {
  border: none;
  background: transparent;
  color: #ef4444;
  font-weight: 600;
}

.tag-list {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.tag-list button {
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 999px;
  padding: 0.35rem 0.9rem;
  background: transparent;
  font-weight: 500;
  color: #475569;
}

.tag-list button.active {
  background: rgba(37, 99, 235, 0.1);
  color: #2563eb;
  border-color: rgba(37, 99, 235, 0.2);
}

.sort-panel ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.sort-panel label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.result-headline {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0;
}

.result-headline h2 {
  margin: 0.25rem 0 0;
  color: #0f172a;
}

.clear-btn {
  border: none;
  background: transparent;
  color: #2563eb;
  font-weight: 600;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1.25rem;
}

.empty-state {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.empty-state button {
  border: none;
  border-radius: 999px;
  padding: 0.6rem 1.5rem;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}

@media (max-width: 640px) {
  .search-section {
    flex-direction: column;
  }

  .search-actions {
    width: 100%;
    justify-content: space-between;
  }

  .ghost-btn {
    flex: 1;
    text-align: center;
  }
}
</style>
