import { defineStore } from 'pinia';
import { mockPosts } from '@/data/posts';

export const usePostStore = defineStore('posts', {
  state: () => ({
    posts: mockPosts,
    selectedPost: null
  }),
  getters: {
    allPosts: (state) => state.posts,
    getPostById: (state) => (id) => state.posts.find((post) => post.id === id)
  },
  actions: {
    toggleLike(postId) {
      const post = this.posts.find((item) => item.id === postId);
      if (!post) return;
      post.liked = !post.liked;
      post.likes += post.liked ? 1 : -1;
    },
    addComment(postId, content) {
      const post = this.posts.find((item) => item.id === postId);
      if (!post) return;
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
      post.commentsThread.unshift(newComment);
      post.comments += 1;
    },
    createPost(payload) {
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
    },
    toggleFollow(postId) {
      const post = this.posts.find((item) => item.id === postId);
      if (!post) return;
      post.isFollowing = !post.isFollowing;
    }
  }
});
