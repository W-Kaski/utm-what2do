<template>
  <section class="user-page" v-if="author">
    <header class="hero">
      <img class="avatar" :src="author.avatar" :alt="author.name" />
      <div>
        <p class="eyebrow">Community member</p>
        <h1>{{ author.name }}</h1>
        <p>{{ author.bio || 'This student has not added a bio yet.' }}</p>
      </div>
    </header>

    <section class="panel">
      <header class="panel__header">
        <div>
          <p class="eyebrow eyebrow--soft">Posts</p>
          <h2>Recent posts</h2>
        </div>
      </header>

      <div class="posts">
        <PostCard
          v-for="post in userPosts"
          :key="post.id"
          :post="post"
          :collapsible="false"
          @open-detail="openDetail"
        />
        <p v-if="!userPosts.length" class="empty">No posts yet.</p>
      </div>
    </section>
  </section>
  <p v-else class="empty">User not found.</p>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PostCard from '@/components/PostCard.vue';
import { usePostStore } from '@/stores/posts';

const route = useRoute();
const router = useRouter();
const postStore = usePostStore();

const userPosts = computed(() =>
  postStore.allPosts.filter((post) => post.author?.id === route.params.id)
);

const author = computed(() => userPosts.value[0]?.author ?? null);

const openDetail = (id) => {
  router.push({ name: 'post-detail', params: { id } });
};
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.hero {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  display: flex;
  gap: 1rem;
  align-items: center;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
}

.avatar {
  width: 96px;
  height: 96px;
  border-radius: 999px;
  object-fit: cover;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.15em;
  font-size: 0.75rem;
  color: #94a3b8;
  margin: 0;
}

.hero h1 {
  margin: 0.25rem 0;
}

.panel {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
}

.panel__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.posts {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.empty {
  color: #94a3b8;
  text-align: center;
}
</style>
