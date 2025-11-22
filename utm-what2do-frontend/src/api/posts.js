import apiClient from './index';

// Transform backend post response to frontend format
const transformPost = (post) => {
  if (!post) return null;
  return {
    ...post,
    likes: post.likesCount || 0,
    comments: post.commentsCount || 0,
    reposts: post.repostsCount || 0,
    author: {
      id: post.userId,
      name: post.username,
      avatar: post.avatarUrl
    }
  };
};

// Transform backend comment response to frontend format
const transformComment = (comment) => {
  if (!comment) return null;
  return {
    ...comment,
    likes: comment.likesCount || 0,
    author: {
      id: comment.userId,
      name: comment.username,
      avatar: comment.avatarUrl
    }
  };
};

export const postsService = {
  async getPosts(params = {}) {
    const response = await apiClient.get('/posts', { params });
    if (response.data?.data?.records) {
      response.data.data.records = response.data.data.records.map(transformPost);
    }
    return response.data;
  },

  async getPostById(id) {
    const response = await apiClient.get(`/posts/${id}`);
    if (response.data?.data) {
      response.data.data = transformPost(response.data.data);
    }
    return response.data;
  },

  async createPost(postData) {
    const response = await apiClient.post('/posts', postData);
    return response.data;
  },

  async deletePost(id) {
    const response = await apiClient.delete(`/posts/${id}`);
    return response.data;
  },

  async likePost(id) {
    const response = await apiClient.post(`/posts/${id}/like`);
    return response.data;
  },

  async unlikePost(id) {
    const response = await apiClient.delete(`/posts/${id}/like`);
    return response.data;
  },

  // Fixed: Comment endpoints now use /comments/* paths
  async getComments(postId) {
    const response = await apiClient.get(`/comments/post/${postId}`);
    if (response.data?.data) {
      if (Array.isArray(response.data.data)) {
        response.data.data = response.data.data.map(transformComment);
      } else if (response.data.data.records) {
        response.data.data.records = response.data.data.records.map(transformComment);
      }
    }
    return response.data;
  },

  async addComment(postId, content) {
    const response = await apiClient.post('/comments', {
      postId: postId,
      content: content
    });
    return response.data;
  },

  async deleteComment(postId, commentId) {
    const response = await apiClient.delete(`/comments/${commentId}`);
    return response.data;
  },

  async repost(id) {
    const response = await apiClient.post(`/posts/${id}/repost`);
    return response.data;
  }
};

export default postsService;
