<template>
  <div class="feed-layout">
    <FeedSidebar active="search" @following="goToFollowing" @home="goHome" />
    <section class="search-page">
      <div class="search-bar">
        <span>🔎</span>
        <input
          v-model="query"
          type="search"
          placeholder="Search posts / users / tags / events"
          @focus="showSuggestions = true"
          @keyup.enter="handleSearch"
        />
        <button class="primary-btn" type="button" @click="handleSearch">Search</button>
        <div v-if="showSuggestions && suggestions.length" class="suggestions">
          <p>Smart suggestions</p>
          <button
            v-for="suggestion in suggestions"
            :key="suggestion.value"
            type="button"
            @click="selectSuggestion(suggestion.value)"
          >
            {{ suggestion.label }}
          </button>
        </div>
      </div>

      <div class="tabs">
        <button
          v-for="tab in tabs"
        :key="tab.id"
        type="button"
        :class="{ active: activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        {{ tab.label }}
      </button>
      </div>

      <div class="results">
        <template v-if="activeTab === 'posts'">
          <PostCard
          v-for="post in filteredPosts"
          :key="post.id"
          :post="post"
          @open-detail="goToPost"
        />
      </template>

      <template v-else-if="activeTab === 'events'">
        <article v-for="event in filteredEvents" :key="event.id" class="event-result-card" @click="goToEvent(event.id)">
          <img v-if="event.coverImage" :src="event.coverImage" :alt="event.title" class="event-thumb" />
          <div class="event-info">
            <h3>{{ event.title }}</h3>
            <p>{{ event.description }}</p>
            <div class="event-meta">
              <span>{{ event.date }}</span>
              <span>{{ event.location }}</span>
            </div>
          </div>
        </article>
      </template>

      <template v-else-if="activeTab === 'users'">
        <article v-for="club in filteredClubs" :key="club.id" class="user-card" @click="goToClub(club.id)">
          <img :src="club.logo" :alt="club.name" />
          <div>
            <h3>{{ club.name }}</h3>
            <p>{{ club.tagline }}</p>
          </div>
          <span>{{ club.stats.members }} members</span>
        </article>
      </template>

      <template v-else-if="activeTab === 'tags'">
        <article v-for="tag in filteredTags" :key="tag.name" class="tag-card" @click="goToTag(tag.name)">
          <strong>#{{ tag.name }}</strong>
          <p>{{ tag.count }} posts · {{ tag.views }} views</p>
        </article>
      </template>

        <p v-if="!hasResults" class="empty-state">No results found. Try another keyword.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import PostCard from '@/components/PostCard.vue';
import { usePostStore } from '@/stores/posts';
import { useEventStore } from '@/stores/events';
import { useClubStore } from '@/stores/clubs';
import { clubs } from '@/data/clubs';
import FeedSidebar from '@/components/FeedSidebar.vue';

const router = useRouter();
const postStore = usePostStore();
const eventStore = useEventStore();
const clubStore = useClubStore();

const query = ref('');
const showSuggestions = ref(false);
const suggestions = ref([]);
const activeTab = ref('posts');

const tabs = [
  { id: 'posts', label: 'Posts' },
  { id: 'events', label: 'Events' },
  { id: 'users', label: 'Clubs' },
  { id: 'tags', label: 'Tags' }
];

const filteredEvents = computed(() => {
  const term = query.value.trim().toLowerCase();
  return eventStore.events.filter(
    (event) =>
      event.title.toLowerCase().includes(term) ||
      event.description.toLowerCase().includes(term) ||
      (event.tags || []).some(tag => tag.toLowerCase().includes(term))
  );
});

const filteredPosts = computed(() => {
  const term = query.value.trim().toLowerCase();
  return postStore.allPosts.filter((post) => post.content.toLowerCase().includes(term));
});

const filteredClubs = computed(() => {
  const term = query.value.trim().toLowerCase();
  return clubs.filter(
    (club) =>
      club.name.toLowerCase().includes(term) ||
      club.tagline.toLowerCase().includes(term) ||
      club.blurb.toLowerCase().includes(term)
  );
});

const tagStats = computed(() => {
  const stats = {};
  postStore.allPosts.forEach((post) => {
    (post.tags || []).forEach((tag) => {
      if (!stats[tag]) {
        stats[tag] = { name: tag, count: 0, views: Math.round(Math.random() * 500 + 200) };
      }
      stats[tag].count += 1;
    });
  });
  return Object.values(stats);
});

const filteredTags = computed(() => {
  const term = query.value.replace('#', '').trim().toLowerCase();
  return tagStats.value.filter((tag) => tag.name.toLowerCase().includes(term));
});

const hasResults = computed(() => {
  if (activeTab.value === 'posts') return filteredPosts.value.length > 0;
  if (activeTab.value === 'events') return filteredEvents.value.length > 0;
  if (activeTab.value === 'users') return filteredClubs.value.length > 0;
  return filteredTags.value.length > 0;
});

const goToEvent = (id) => {
  router.push({ name: 'event-detail', params: { id } });
};

watch(query, (value) => {
  if (!value) {
    suggestions.value = [];
    showSuggestions.value = false;
    return;
  }
  const term = value.toLowerCase();
  const matchedTags = tagStats.value
    .filter((tag) => tag.name.toLowerCase().includes(term))
    .map((tag) => ({ label: `#${tag.name}`, value: `#${tag.name}` }));
  const matchedUsers = clubs
    .filter((club) => club.name.toLowerCase().includes(term))
    .slice(0, 3)
    .map((club) => ({ label: `@${club.name}`, value: club.name }));
  suggestions.value = [...matchedTags.slice(0, 3), ...matchedUsers];
  showSuggestions.value = true;
});

const handleSearch = () => {
  const term = query.value.trim();
  if (!term) return;
  showSuggestions.value = false;
};

const selectSuggestion = (val) => {
  query.value = val.replace(/^#|@/, '');
  handleSearch();
};

const clearQuery = () => {
  query.value = '';
  showSuggestions.value = false;
};

const goToPost = (id) => {
  router.push({ name: 'post-detail', params: { id } });
};

const goToClub = (id) => {
  router.push({ name: 'club-detail', params: { id } });
};

const goToTag = (name) => {
  router.push({ name: 'explore', query: { tag: name } });
};

const goHome = () => {
  router.push({ name: 'feed' }).catch(() => {});
};

const goToFollowing = () => {
  router.push({ name: 'feed', query: { tab: 'following' } }).catch(() => {});
};
</script>

<style scoped>
.feed-layout {
  display: flex;
  gap: 1.5rem;
}

.search-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #fff;
  border-radius: 999px;
  box-shadow: 0 18px 35px rgba(15, 23, 42, 0.08);
}

.search-bar input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 1rem;
}

.primary-btn {
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: #fff;
  padding: 0.45rem 1.1rem;
  font-weight: 600;
}

.suggestions {
  position: absolute;
  top: 54px;
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 1rem;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.suggestions p {
  margin: 0;
  color: #94a3b8;
  font-size: 0.85rem;
}

.suggestions button {
  border: none;
  background: transparent;
  text-align: left;
  padding: 0.35rem 0;
  color: #1d4ed8;
  font-weight: 600;
}

.tabs {
  display: flex;
  gap: 0.5rem;
}

.tabs button {
  border: none;
  background: #fff;
  border-radius: 999px;
  padding: 0.4rem 1.2rem;
  font-weight: 600;
  color: #475569;
}

.tabs button.active {
  background: rgba(37, 99, 235, 0.15);
  color: #1d4ed8;
}

.results {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-card,
.tag-card,
.event-card {
  background: #fff;
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  gap: 1rem;
  align-items: center;
  cursor: pointer;
}

.user-card img {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  object-fit: cover;
}

.event-result-card {
  background: #fff;
  border-radius: 1rem;
  padding: 1rem;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  gap: 1rem;
  cursor: pointer;
  transition: transform 0.15s ease;
}

.event-result-card:hover {
  transform: translateY(-2px);
}

.event-thumb {
  width: 120px;
  height: 80px;
  border-radius: 0.5rem;
  object-fit: cover;
}

.event-info h3 {
  margin: 0 0 0.25rem;
  color: #0f172a;
}

.event-info p {
  margin: 0 0 0.5rem;
  color: #64748b;
  font-size: 0.875rem;
}

.event-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.8rem;
  color: #94a3b8;
}

.event-card header {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.event-card footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
  font-size: 0.9rem;
  color: #475569;
}

.empty-state {
  text-align: center;
  color: #94a3b8;
  margin-top: 2rem;
}
</style>
