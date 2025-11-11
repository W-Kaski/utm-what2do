<template>
  <section v-if="post" class="post-detail">
    <button class="back-btn" type="button" @click="goBack">← 返回动态</button>
    <PostCard :post="post" :collapsible="false" @open-detail.prevent />

    <div class="comment-section">
      <h3>评论 · {{ post.commentsThread.length }}</h3>
      <form class="comment-form" @submit.prevent="submitComment">
        <input v-model="commentContent" type="text" maxlength="200" placeholder="发表你的看法..." />
        <button type="submit" :disabled="!commentContent.trim()">发送</button>
      </form>

      <div class="comments">
        <article v-for="comment in post.commentsThread" :key="comment.id" class="comment">
          <img :src="comment.author.avatar" :alt="comment.author.name" />
          <div>
            <div class="comment-meta">
              <strong>{{ comment.author.name }}</strong>
              <span>{{ formatRelative(comment.createdAt) }}</span>
            </div>
            <p>{{ comment.content }}</p>
            <div class="comment-actions">
              <button type="button">👍 {{ comment.likes }}</button>
              <button type="button">回复</button>
            </div>
            <div class="reply" v-for="reply in comment.replies" :key="reply.id">
              <img :src="reply.author.avatar" :alt="reply.author.name" />
              <div>
                <div class="comment-meta">
                  <strong>{{ reply.author.name }}</strong>
                  <span>{{ formatRelative(reply.createdAt) }}</span>
                </div>
                <p>{{ reply.content }}</p>
              </div>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
  <p v-else>正在加载动态...</p>
</template>

<script setup>
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PostCard from '@/components/PostCard.vue';
import { usePostStore } from '@/stores/posts';

const route = useRoute();
const router = useRouter();
const postStore = usePostStore();

const post = computed(() => postStore.getPostById(route.params.id));
const commentContent = ref('');

const submitComment = () => {
  if (!commentContent.value.trim()) return;
  postStore.addComment(route.params.id, commentContent.value.trim());
  commentContent.value = '';
};

const goBack = () => {
  router.back();
};

const formatRelative = (value) => {
  const date = new Date(value);
  const diff = Date.now() - date.getTime();
  const hours = Math.round(diff / (1000 * 60 * 60));
  if (hours < 1) return '刚刚';
  if (hours < 24) return `${hours} 小时前`;
  const days = Math.round(hours / 24);
  return `${days} 天前`;
};
</script>

<style scoped>
.post-detail {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.back-btn {
  border: none;
  background: transparent;
  color: #2563eb;
  font-weight: 600;
  width: max-content;
}

.comment-section {
  background: #fff;
  border-radius: 1.5rem;
  padding: 1.5rem;
  box-shadow: 0 25px 45px rgba(15, 23, 42, 0.08);
}

.comment-form {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
}

.comment-form input {
  flex: 1;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  padding: 0.5rem 1rem;
}

.comment-form button {
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: #fff;
  padding: 0.5rem 1.2rem;
  font-weight: 600;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment {
  display: flex;
  gap: 0.75rem;
}

.comment img {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  object-fit: cover;
}

.comment-meta {
  display: flex;
  gap: 0.5rem;
  color: #94a3b8;
  font-size: 0.85rem;
}

.comment-actions {
  display: flex;
  gap: 0.75rem;
}

.comment-actions button {
  border: none;
  background: transparent;
  color: #475569;
  font-size: 0.85rem;
}

.reply {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  padding-left: 2.5rem;
}

.reply img {
  width: 32px;
  height: 32px;
}
</style>
