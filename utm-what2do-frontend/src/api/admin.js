import apiClient from './index';

// ==================== 用户管理 ====================

export const getUserList = (params) => {
  return apiClient.get('/admin/users', { params });
};

export const updateUserRole = (userId, role) => {
  return apiClient.put(`/admin/users/${userId}/role`, { role });
};

export const updateUserStatus = (userId, deleted) => {
  return apiClient.put(`/admin/users/${userId}/status`, { deleted });
};

// ==================== 社团管理 ====================

export const addClubManager = (clubId, userId) => {
  return apiClient.post(`/admin/clubs/${clubId}/manager`, { userId });
};

export const removeClubManager = (clubId, userId) => {
  return apiClient.delete(`/admin/clubs/${clubId}/manager`, { data: { userId } });
};

export const deleteClub = (clubId) => {
  return apiClient.delete(`/admin/clubs/${clubId}`);
};

// ==================== 活动管理 ====================

export const markEventOfficial = (eventId, isOfficial) => {
  return apiClient.put(`/admin/events/${eventId}/official`, { isOfficial });
};

export const deleteEvent = (eventId) => {
  return apiClient.delete(`/admin/events/${eventId}`);
};

// ==================== 内容审核 ====================

export const deletePost = (postId) => {
  return apiClient.delete(`/admin/posts/${postId}`);
};

export const deleteComment = (commentId) => {
  return apiClient.delete(`/admin/comments/${commentId}`);
};

// ==================== 系统维护 ====================

export const clearCache = (type) => {
  return apiClient.post('/admin/cache/clear', null, { params: { type } });
};

export const getSystemHealth = () => {
  return apiClient.get('/admin/health');
};

export default {
  getUserList,
  updateUserRole,
  updateUserStatus,
  addClubManager,
  removeClubManager,
  deleteClub,
  markEventOfficial,
  deleteEvent,
  deletePost,
  deleteComment,
  clearCache,
  getSystemHealth
};
