<template>
  <div class="feed-page">
    <FeedSidebar :active="activeNav" @following="showFollowing" @home="showHome" />

    <section class="feed-content">
      <header class="feed-header">
        <div class="tabs">
          <button type="button" :class="{ active: activeTab === 'all' }" @click="setTab('all')">推荐</button>
          <button type="button" :class="{ active: activeTab === 'following' }" @click="setTab('following')">
            关注
          </button>
        </div>
      </header>

      <div class="posts">
        <PostCard
          v-for="post in visiblePosts"
          :key="post.id"
          :post="post"
          @open-detail="openDetail"
          @open-comments="openComments"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import FeedSidebar from '@/components/FeedSidebar.vue';
import PostCard from '@/components/PostCard.vue';
import { usePostStore } from '@/stores/posts';

const router = useRouter();
const route = useRoute();
const postStore = usePostStore();

const activeTab = ref('all');
const activeNav = ref('home');

const visiblePosts = computed(() => {
  if (activeTab.value === 'following') {
    return postStore.allPosts.slice(0, 1);
  }
  return postStore.allPosts;
});

const openDetail = (id) => {
  router.push({ name: 'post-detail', params: { id } });
};

const openComments = (id) => {
  openDetail(id);
};

const setTab = (tab) => {
  activeTab.value = tab;
  activeNav.value = tab === 'following' ? 'following' : 'home';

  const query = { ...route.query };
  if (tab === 'following') {
    query.tab = 'following';
  } else {
    delete query.tab;
  }
  router.replace({ name: 'feed', query }).catch(() => {});
};

const showFollowing = () => {
  setTab('following');
};

const showHome = () => {
  setTab('all');
};

watch(
  () => route.query.tab,
  (tab) => {
    if (tab === 'following') {
      activeTab.value = 'following';
      activeNav.value = 'following';
    } else {
      activeTab.value = 'all';
      activeNav.value = 'home';
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.feed-page {
  display: flex;
  gap: 1.5rem;
  min-height: 100%;
}

.feed-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tabs {
  background: #fff;
  border-radius: 999px;
  padding: 0.25rem;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.4);
}

.tabs button {
  border: none;
  background: transparent;
  padding: 0.45rem 1.2rem;
  border-radius: 999px;
  font-weight: 600;
  color: #475569;
}

.tabs button.active {
  background: rgba(37, 99, 235, 0.15);
  color: #1d4ed8;
}

.posts {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
