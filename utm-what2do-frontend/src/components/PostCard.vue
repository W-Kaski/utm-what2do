<template>
  <article class="post-card">
    <header class="post-header">
      <button class="avatar" type="button" @click="goToProfile(post.author.id)">
        <img :src="post.author.avatar" :alt="post.author.name" />
      </button>
      <div class="author-info">
        <strong>{{ post.author.name }}</strong>
        <p>{{ relativeTime }}</p>
      </div>
      <button type="button" class="follow-btn" @click.stop="toggleFollow">
        {{ post.isFollowing ? '已关注' : '关注' }}
      </button>
      <button type="button" class="icon-btn">···</button>
    </header>

    <div class="post-body" @click="handleOpenDetail">
      <p>
        <span v-if="isCollapsed">{{ truncatedText }}</span>
        <span v-else>{{ post.content }}</span>
        <button v-if="shouldTruncate" type="button" class="inline-link" @click.stop="toggleText">
          {{ isCollapsed ? '展开' : '收起' }}
        </button>
      </p>

      <div v-if="post.media?.type === 'images'" class="media-grid" :class="`count-${mediaItems.length}`">
        <button
          v-for="(image, index) in mediaItems"
          :key="image"
          type="button"
          class="media-cell"
          @click.stop="openLightbox(index)"
        >
          <img :src="image" alt="" />
          <span v-if="index === maxDisplay - 1 && remainingCount > 0" class="overlay">+{{ remainingCount }}</span>
        </button>
      </div>

      <div v-else-if="post.media?.type === 'video'" class="video-shell">
        <video ref="videoRef" controls :poster="post.media.thumbnail">
          <source :src="post.media.items[0]" type="video/mp4" />
          Your browser does not support video playback.
        </video>
      </div>
    </div>

    <footer class="action-bar">
      <button type="button" @click.stop="toggleLike">
        ❤️ <span>{{ post.likes }}</span>
      </button>
      <button type="button" @click.stop="handleComments">
        💬 <span>{{ post.comments }}</span>
      </button>
      <button type="button" @click.stop="handleRepost">
        🔁 <span>{{ post.reposts }}</span>
      </button>
    </footer>

    <div v-if="lightboxOpen" class="lightbox" @click="closeLightbox">
      <button type="button" class="close-btn" @click.stop="closeLightbox">×</button>
      <img :src="mediaItems[activeImage]" alt="" />
      <div class="lightbox-nav" v-if="mediaItems.length > 1">
        <button type="button" @click.stop="prevImage">‹</button>
        <button type="button" @click.stop="nextImage">›</button>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { usePostStore } from '@/stores/posts';

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  collapsible: {
    type: Boolean,
    default: true
  },
  maxLength: {
    type: Number,
    default: 220
  }
});

const emits = defineEmits(['open-detail', 'open-comments']);

const router = useRouter();
const postStore = usePostStore();

const isCollapsed = ref(true);
const videoRef = ref(null);
const lightboxOpen = ref(false);
const activeImage = ref(0);
const maxDisplay = 9;

const shouldTruncate = computed(() => props.collapsible && props.post.content.length > props.maxLength);
const truncatedText = computed(() =>
  shouldTruncate.value ? `${props.post.content.slice(0, props.maxLength)}...` : props.post.content
);

const mediaItems = computed(() => {
  if (!props.post.media?.items?.length) return [];
  return props.post.media.items.slice(0, maxDisplay);
});

const remainingCount = computed(() => {
  if (!props.post.media?.items?.length) return 0;
  return Math.max(0, props.post.media.items.length - maxDisplay);
});

const relativeTime = computed(() => {
  const value = props.post.createdAt ? new Date(props.post.createdAt) : new Date();
  const diff = Date.now() - value.getTime();
  const hours = Math.round(diff / (1000 * 60 * 60));
  if (hours < 1) return '刚刚';
  if (hours < 24) return `${hours} 小时前`;
  const days = Math.round(hours / 24);
  return `${days} 天前`;
});

const toggleText = () => {
  isCollapsed.value = !isCollapsed.value;
};

const handleOpenDetail = () => {
  emits('open-detail', props.post.id);
};

const handleComments = () => {
  emits('open-comments', props.post.id);
};

const toggleLike = () => {
  postStore.toggleLike(props.post.id);
};

const toggleFollow = () => {
  postStore.toggleFollow(props.post.id);
};

const handleRepost = () => {
  alert('转发功能将连接到发布窗口，敬请期待。');
};

const goToProfile = (id) => {
  router.push({ name: 'profile', query: { user: id } });
};

const openLightbox = (index) => {
  lightboxOpen.value = true;
  activeImage.value = index;
};

const closeLightbox = () => {
  lightboxOpen.value = false;
};

const prevImage = () => {
  activeImage.value = (activeImage.value - 1 + mediaItems.value.length) % mediaItems.value.length;
};

const nextImage = () => {
  activeImage.value = (activeImage.value + 1) % mediaItems.value.length;
};
</script>

<style scoped>
.post-card {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 999px;
  border: none;
  padding: 0;
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-header strong {
  display: block;
  color: #0f172a;
}

.author-info {
  flex: 1;
}

.follow-btn {
  border-radius: 999px;
  border: 1px solid rgba(37, 99, 235, 0.3);
  padding: 0.2rem 0.85rem;
  font-weight: 600;
  background: #fff;
  color: #2563eb;
}

.follow-btn:hover {
  background: rgba(37, 99, 235, 0.05);
}

.post-header p {
  margin: 0;
  color: #94a3b8;
  font-size: 0.85rem;
}

.icon-btn {
  margin-left: auto;
  border: none;
  background: transparent;
  color: #94a3b8;
  font-size: 1.25rem;
}

.post-body p {
  margin: 0;
  line-height: 1.6;
  color: #1f2937;
}

.inline-link {
  background: transparent;
  border: none;
  color: #2563eb;
  font-weight: 600;
  cursor: pointer;
}

.media-grid {
  margin-top: 0.75rem;
  display: grid;
  gap: 0.4rem;
}

.media-grid.count-1 {
  grid-template-columns: 1fr;
}

.media-grid.count-2,
.media-grid.count-4 {
  grid-template-columns: repeat(2, 1fr);
}

.media-grid.count-3,
.media-grid.count-5,
.media-grid.count-6,
.media-grid.count-7,
.media-grid.count-8,
.media-grid.count-9 {
  grid-template-columns: repeat(3, 1fr);
}

.media-cell {
  border: none;
  padding: 0;
  border-radius: 1rem;
  overflow: hidden;
  position: relative;
  background: #0f172a;
}

.media-cell img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.2rem;
}

.video-shell video {
  width: 100%;
  border-radius: 1rem;
  background: #000;
}

.action-bar {
  display: flex;
  gap: 1rem;
}

.action-bar button {
  border: none;
  background: transparent;
  color: #475569;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-weight: 600;
}

.lightbox {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.lightbox img {
  max-width: 90vw;
  max-height: 90vh;
  border-radius: 1rem;
}

.close-btn {
  position: absolute;
  top: 1.5rem;
  right: 2rem;
  border: none;
  background: transparent;
  color: #fff;
  font-size: 2rem;
  cursor: pointer;
}

.lightbox-nav {
  position: absolute;
  width: 100%;
  top: 50%;
  display: flex;
  justify-content: space-between;
  padding: 0 2rem;
}

.lightbox-nav button {
  border: none;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 2rem;
  width: 48px;
  height: 48px;
  border-radius: 999px;
}
</style>
