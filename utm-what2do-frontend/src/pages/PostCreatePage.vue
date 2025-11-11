<template>
  <div class="feed-layout">
    <FeedSidebar active="create" @home="goHome" @following="goFollowing" />
    <section class="post-create">
      <header>
        <h1>发布动态</h1>
      </header>

      <form class="editor" @submit.prevent="submitPost">
      <textarea
        v-model="content"
        maxlength="500"
        placeholder="分享你在校园的灵感、活动或灵光一现..."
      ></textarea>
      <div class="counter">{{ content.length }}/500</div>

      <div class="media-controls">
        <label>
          <input type="file" accept="image/*" multiple @change="handleImages" :disabled="videoFile" />
          上传图片
        </label>
        <label>
          <input type="file" accept="video/*" @change="handleVideo" :disabled="imageFiles.length" />
          上传视频
        </label>
        <span class="hint">最多 9 张图片或 1 个视频（100MB 内）</span>
      </div>

      <div class="preview-grid" v-if="imagePreviews.length">
        <div v-for="(img, index) in imagePreviews" :key="index" class="preview-cell">
          <img :src="img" alt="" />
          <button type="button" @click="removeImage(index)">×</button>
        </div>
      </div>

      <div v-if="videoPreview" class="video-preview">
        <video controls :src="videoPreview"></video>
        <button type="button" @click="removeVideo">移除</button>
      </div>

        <button class="submit" type="submit" :disabled="!content.trim() && !imageFiles.length && !videoFile">
        发布
      </button>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { usePostStore } from '@/stores/posts';
import FeedSidebar from '@/components/FeedSidebar.vue';

const router = useRouter();
const postStore = usePostStore();

const content = ref('');
const imageFiles = ref([]);
const imagePreviews = ref([]);
const videoFile = ref(null);
const videoPreview = ref(null);

const handleImages = (event) => {
  const files = Array.from(event.target.files || []);
  const allowed = files.slice(0, 9 - imageFiles.value.length);
  allowed.forEach((file) => {
    imageFiles.value.push(file);
    const reader = new FileReader();
    reader.onload = (e) => imagePreviews.value.push(e.target?.result);
    reader.readAsDataURL(file);
  });
  event.target.value = '';
};

const removeImage = (index) => {
  imageFiles.value.splice(index, 1);
  imagePreviews.value.splice(index, 1);
};

const handleVideo = (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  videoFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => (videoPreview.value = e.target?.result);
  reader.readAsDataURL(file);
  event.target.value = '';
};

const removeVideo = () => {
  videoFile.value = null;
  videoPreview.value = null;
};

const submitPost = () => {
  const media = imageFiles.value.length
    ? { type: 'images', items: imagePreviews.value }
    : videoFile.value
      ? { type: 'video', items: [videoPreview.value], thumbnail: videoPreview.value }
      : null;

  const newId = postStore.createPost({
    author: {
      id: 'current',
      name: 'UTM Explorer',
      avatar: 'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80'
    },
    content: content.value,
    media
  });

  router.push({ name: 'post-detail', params: { id: newId } });
};

const goHome = () => {
  router.push({ name: 'feed' }).catch(() => {});
};

const goFollowing = () => {
  router.push({ name: 'feed', query: { tab: 'following' } }).catch(() => {});
};
</script>

<style scoped>
.feed-layout {
  display: flex;
  gap: 1.5rem;
}

.post-create {
  flex: 1;
  max-width: 680px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

textarea {
  min-height: 160px;
  resize: vertical;
  border-radius: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.4);
  padding: 1rem;
  font-size: 1rem;
  font-family: inherit;
}

.counter {
  text-align: right;
  color: #94a3b8;
}

.media-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  color: #475569;
}

.media-controls input {
  display: none;
}

.media-controls label {
  border-radius: 999px;
  border: 1px solid rgba(37, 99, 235, 0.3);
  padding: 0.4rem 1rem;
  font-weight: 600;
  color: #1d4ed8;
  cursor: pointer;
}

.hint {
  color: #94a3b8;
  font-size: 0.9rem;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 0.5rem;
}

.preview-cell {
  position: relative;
  border-radius: 1rem;
  overflow: hidden;
}

.preview-cell img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-cell button {
  position: absolute;
  top: 0.25rem;
  right: 0.25rem;
  border: none;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  border-radius: 999px;
  width: 24px;
  height: 24px;
}

.video-preview video {
  width: 100%;
  border-radius: 1rem;
}

.video-preview button {
  border: none;
  background: transparent;
  color: #dc2626;
  font-weight: 600;
  margin-top: 0.25rem;
}

.submit {
  border: none;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
}

.submit:disabled {
  background: #cbd5f5;
}
</style>
