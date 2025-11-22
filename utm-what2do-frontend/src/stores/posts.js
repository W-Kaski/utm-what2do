import { defineStore } from 'pinia';
import { postsService } from '@/api/posts';
import { mockPosts } from '@/data/posts';

export const usePostStore = defineStore('posts', {
  state: () => ({
    posts: [],
    selectedPost: null,
    loading: false,
    error: null,
    pagination: {
      page: 1,
      pageSize: 10,
      total: 0
    }
  }),
  getters: {
    allPosts: (state) => state.posts,
    getPostById: (state) => (id) => state.posts.find((post) => post.id === id)
  },
  actions: {
    async fetchPosts(params = {}) {
      this.loading = true;
      this.error = null;
      try {
        const response = await postsService.getPosts(params);
        if (response?.data) {
          this.posts = response.data.records || response.data;
          if (response.data.total) {
            this.pagination.total = response.data.total;
          }
        }
      } catch (err) {
        console.error('Failed to fetch posts:', err);
        this.error = err.message || 'Failed to fetch posts';
      } finally {
        this.loading = false;
      }
    },

    async fetchPostById(id) {
      this.loading = true;
      this.error = null;
      try {
        const response = await postsService.getPostById(id);
        if (response?.data) {
          this.selectedPost = response.data;
          return response.data;
        }
      } catch (err) {
        console.error('Failed to fetch post:', err);
        this.error = err.message || 'Failed to fetch post';
      } finally {
        this.loading = false;
      }
      return this.selectedPost;
    },

    async toggleLike(postId) {
      const post = this.posts.find((item) => item.id === postId);
      if (!post) return;

      // Save original state for rollback
      const originalLiked = post.liked;
      const originalLikes = post.likes;

      // Optimistic update
      post.liked = !originalLiked;
      post.likes = originalLiked ? originalLikes - 1 : originalLikes + 1;

      try {
        if (originalLiked) {
          await postsService.unlikePost(postId);
        } else {
          await postsService.likePost(postId);
        }
      } catch (err) {
        console.error('Failed to toggle like:', err);
        // Rollback on error
        post.liked = originalLiked;
        post.likes = originalLikes;
        throw err;
      }
    },

    async addComment(postId, content) {
      const post = this.posts.find((item) => item.id === postId);
      if (!post) return;

      try {
        const response = await postsService.addComment(postId, content);
        if (response?.data) {
          if (!post.commentsThread) {
            post.commentsThread = [];
          }
          post.commentsThread.unshift(response.data);
          post.comments += 1;
          return response.data;
        }
      } catch (err) {
        console.error('Failed to add comment:', err);
        // Fallback for offline mode
        const newComment = {
          id: `c-${Date.now()}`,
          author: {
            id: 'current',
            name: 'UTM Explorer',
            avatar: 'https://images.unsplash.com/photo-1504593811423-6dd665756598?auto=format&fit=crop&w=200&q=80'
          },
          content,
          createdAt: new Date().toISOString(),
          likes: 0,
          replies: []
        };
        if (!post.commentsThread) {
          post.commentsThread = [];
        }
        post.commentsThread.unshift(newComment);
        post.comments += 1;
        return newComment;
      }
    },

    async createPost(payload) {
      this.loading = true;
      this.error = null;
      try {
        const response = await postsService.createPost(payload);
        if (response?.data) {
          this.posts.unshift(response.data);
          return response.data.id;
        }
      } catch (err) {
        console.error('Failed to create post:', err);
        this.error = err.message || 'Failed to create post';
        // Fallback for offline mode
        const newPost = {
          ...payload,
          id: `post-${Date.now()}`,
          likes: 0,
          comments: 0,
          reposts: 0,
          liked: false,
          isFollowing: true,
          createdAt: new Date().toISOString(),
          commentsThread: []
        };
        this.posts.unshift(newPost);
        return newPost.id;
      } finally {
        this.loading = false;
      }
    },

    async deletePost(id) {
      try {
        await postsService.deletePost(id);
        this.posts = this.posts.filter(p => p.id !== id);
      } catch (err) {
        console.error('Failed to delete post:', err);
        throw err;
      }
    },

    async toggleFollow(postId) {
      const post = this.posts.find((item) => item.id === postId);
      if (!post) return;
      post.isFollowing = !post.isFollowing;
    },

    clearError() {
      this.error = null;
    }
  }
});
