<template>
  <section class="filters">
    <input v-model="searchTerm" type="text" placeholder="搜索活动或社团" />
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
    <button class="clear" @click="clear">清空筛选</button>
  </section>
</template>

<script setup>
import { storeToRefs } from 'pinia';
import { useFilterStore } from '@/stores/filters';

const filterStore = useFilterStore();
const { selectedTags, searchTerm } = storeToRefs(filterStore);

const availableTags = ['技术', '社交', '竞赛', '音乐'];

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
