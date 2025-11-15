<template>
  <section class="filters">
    <input v-model="searchTerm" type="text" placeholder="Search events or clubs" />
    <div class="tags">
      <button
        v-for="tag in availableTags"
        :key="tag"
        :class="{ active: selectedTags.includes(tag) }"
        @click="toggle(tag)"
      >
        {{ tag }}
      </button>
    </div>
    <button class="clear" @click="clear">Clear filters</button>
  </section>
</template>

<script setup>
import { storeToRefs } from 'pinia';
import { useFilterStore } from '@/stores/filters';

const filterStore = useFilterStore();
const { selectedTags, searchTerm } = storeToRefs(filterStore);

const availableTags = ['Tech', 'Social', 'Competition', 'Music'];

function toggle(tag) {
  filterStore.toggleTag(tag);
}

function clear() {
  filterStore.clear();
}
</script>

<style scoped>
.filters {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background: #fff;
  padding: 1rem;
  border-radius: 0.75rem;
}

.tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

button.active {
  background: #1d4ed8;
}

.clear {
  align-self: flex-start;
  background: #ef4444;
}
</style>
