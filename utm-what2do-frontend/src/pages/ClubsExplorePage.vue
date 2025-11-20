<template>
  <div class="clubs-explore">
    <aside class="category-panel">
      <p class="panel-title">Club Category</p>
      <ul>
        <li v-for="category in categories" :key="category.id">
          <button
            type="button"
            :class="['category-btn', { active: category.id === selectedCategory }]"
            @click="selectCategory(category.id)"
          >
            <div>
              <strong>{{ category.label }}</strong>
              <span>{{ category.description }}</span>
            </div>
            <span aria-hidden="true">›</span>
          </button>
        </li>
      </ul>
    </aside>

    <section class="explore-body">
      <header class="explore-header">
        <div>
          <p class="eyebrow">Explore Clubs</p>
          <h1>Find the communities that fuel you</h1>
        </div>
        <span class="result-count">{{ filteredClubs.length }} results</span>
      </header>

      <div class="search-bar">
        <label class="sr-only" for="club-search">Search clubs</label>
        <input
          id="club-search"
          v-model="search"
          type="search"
          placeholder="What would you like to explore?"
        />
        <button type="button" @click="resetFilters">Reset</button>
      </div>

      <div class="club-list">
        <article
          v-for="club in filteredClubs"
          :key="club.id"
          class="club-card"
          @click="goToClub(club.id)"
        >
          <img :src="club.logo" :alt="club.name" />
          <div class="club-card__main">
            <header>
              <h3>{{ club.name }}</h3>
              <p>{{ club.tagline }}</p>
            </header>
            <p class="club-card__blurb">{{ club.blurb }}</p>
            <div class="club-card__tags">
              <span v-for="tag in club.tags" :key="tag">{{ tag }}</span>
            </div>
          </div>
          <div class="club-card__stats">
            <p><strong>{{ club.stats.members }}</strong><span>Members</span></p>
            <p><strong>{{ club.stats.events }}</strong><span>Events</span></p>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { clubCategories } from '@/data/clubs';
import { useClubStore } from '@/stores/clubs';
import { HOT_CLUB_THRESHOLD } from '@/constants/highlights';

const clubStore = useClubStore();

const router = useRouter();
const route = useRoute();
const search = ref('');

const hotFilterActive = computed(() => {
  const filter = typeof route.query.filter === 'string' ? route.query.filter : '';
  return filter === 'hot-clubs' || filter === 'hot';
});

const selectedCategory = ref(hotFilterActive.value ? null : clubCategories[0]?.id ?? null);

watch(hotFilterActive, (active) => {
  if (active) {
    selectedCategory.value = null;
  }
});

const categories = clubCategories;

onMounted(() => {
  clubStore.fetchClubs();
});

const filteredClubs = computed(() => {
  const term = search.value.trim().toLowerCase();
  return clubStore.allClubs.filter((club) => {
    const categoryMatch = selectedCategory.value ? club.category === selectedCategory.value : true;
    const searchMatch =
      !term ||
      club.name.toLowerCase().includes(term) ||
      (club.blurb || '').toLowerCase().includes(term) ||
      (club.tags || []).some((tag) => tag.toLowerCase().includes(term));
    const hotMatch = hotFilterActive.value ? Number(club.stats?.members || club.membersCount || 0) >= HOT_CLUB_THRESHOLD : true;
    return categoryMatch && searchMatch && hotMatch;
  });
});

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId;
};

const resetFilters = () => {
  search.value = '';
  selectedCategory.value = hotFilterActive.value ? null : clubCategories[0]?.id ?? null;
};

const goToClub = (id) => {
  router.push({ name: 'club-detail', params: { id } });
};
</script>

<style scoped>
.clubs-explore {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 1.5rem;
}

.category-panel {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem 1rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
}

.panel-title {
  margin: 0 0 1rem;
  font-weight: 700;
  color: #111827;
}

.category-panel ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.category-btn {
  width: 100%;
  border: none;
  background: #f8fafc;
  border-radius: 1rem;
  padding: 0.75rem 1rem;
  text-align: left;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  color: #1e293b;
  transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.category-btn span {
  font-size: 0.8rem;
  color: #94a3b8;
}

.category-btn strong {
  display: block;
  margin-bottom: 0.1rem;
}

.category-btn.active {
  background: linear-gradient(120deg, #dbeafe, #eef2ff);
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.2);
}

.explore-body {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.explore-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0;
}

h1 {
  margin: 0.25rem 0 0;
  font-size: 1.75rem;
  color: #0f172a;
}

.result-count {
  color: #475569;
  font-weight: 600;
}

.search-bar {
  display: flex;
  gap: 0.75rem;
  border: 1px solid rgba(148, 163, 184, 0.4);
  border-radius: 999px;
  padding: 0.5rem 0.75rem;
}

.search-bar input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 1rem;
}

.search-bar button {
  border: none;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #2563eb;
  padding: 0.4rem 1rem;
  font-weight: 600;
}

.club-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.club-card {
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 1.25rem;
  padding: 1rem 1.25rem;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 1.25rem;
  align-items: center;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.club-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 18px 25px rgba(15, 23, 42, 0.08);
}

.club-card img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e2e8f0;
}

.club-card__main header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #0f172a;
}

.club-card__main header p {
  margin: 0.15rem 0 0;
  color: #64748b;
}

.club-card__blurb {
  margin: 0.35rem 0;
  color: #475569;
}

.club-card__tags {
  display: flex;
  gap: 0.35rem;
  flex-wrap: wrap;
}

.club-card__tags span {
  font-size: 0.8rem;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
}

.club-card__stats {
  text-align: right;
  color: #475569;
}

.club-card__stats p {
  margin: 0;
}

.club-card__stats strong {
  display: block;
  font-size: 1.2rem;
  color: #0f172a;
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

@media (max-width: 960px) {
  .clubs-explore {
    grid-template-columns: 1fr;
  }

  .category-panel {
    display: flex;
    overflow-x: auto;
  }

  .category-panel ul {
    flex-direction: row;
  }

  .category-btn {
    min-width: 200px;
  }

  .club-card {
    grid-template-columns: auto 1fr;
  }

  .club-card__stats {
    display: none;
  }
}
</style>
