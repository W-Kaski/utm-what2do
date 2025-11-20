import apiClient from './index';

export const postsService = {
  async getPosts(params = {}) {
    const response = await apiClient.get('/posts', { params });
    return response.data;
  },

  async getPostById(id) {
    const response = await apiClient.get(`/posts/${id}`);
    return response.data;
  },

  async createPost(postData) {
    const response = await apiClient.post('/posts', postData);
    return response.data;
  },

  async updatePost(id, postData) {
    const response = await apiClient.put(`/posts/${id}`, postData);
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

  async getComments(postId) {
    const response = await apiClient.get(`/posts/${postId}/comments`);
    return response.data;
  },

  async addComment(postId, content) {
    const response = await apiClient.post(`/posts/${postId}/comments`, { content });
    return response.data;
  },

  async deleteComment(postId, commentId) {
    const response = await apiClient.delete(`/posts/${postId}/comments/${commentId}`);
    return response.data;
  },

  async repost(id) {
    const response = await apiClient.post(`/posts/${id}/repost`);
    return response.data;
  }
};

export default postsService;
