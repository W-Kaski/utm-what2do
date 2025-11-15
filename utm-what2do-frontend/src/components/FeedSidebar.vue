<template>
  <aside class="feed-sidebar">
    <nav>
      <button type="button" :class="{ active: active === 'home' }" @click="goHome">
        🏠 Home
      </button>
      <button type="button" :class="{ active: active === 'search' }" @click="goSearch">
        🔍 Search
      </button>
      <button type="button" :class="{ active: active === 'following' }" @click="goFollowing">
        👥 Following
      </button>
      <button type="button" :class="{ active: active === 'create' }" @click="goCreate">
        ✏️ Create
      </button>
    </nav>
  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router';

defineProps({
  active: {
    type: String,
    default: 'home'
  }
});

const emit = defineEmits(['following', 'home']);

const router = useRouter();

const goHome = () => {
  router.push({ name: 'feed' }).catch(() => {});
  emit('home');
};

const goSearch = () => {
  router.push({ name: 'search' }).catch(() => {});
};

const goCreate = () => {
  router.push({ name: 'post-create' }).catch(() => {});
};

const goFollowing = () => {
  router.push({ name: 'feed', query: { tab: 'following' } }).catch(() => {});
  emit('following');
};
</script>

<style scoped>
.feed-sidebar {
  width: 200px;
  padding: 1rem 0;
}

nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

button {
  border: none;
  background: transparent;
  font-weight: 600;
  padding: 0.65rem 1rem;
  border-radius: 999px;
  text-align: left;
  color: #475569;
}

button.active {
  background: rgba(37, 99, 235, 0.12);
  color: #1d4ed8;
}
</style>
