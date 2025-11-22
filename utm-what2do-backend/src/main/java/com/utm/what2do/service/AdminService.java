package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.vo.UserInfoVO;

import java.util.Map;

/**
 * 管理员模块Service接口
 */
public interface AdminService {

    // ==================== 用户与社团关系管理 ====================

    /**
     * 修改用户角色
     */
    void updateUserRole(Long userId, String role, Long adminId);

    /**
     * 禁用/解封用户
     */
    void updateUserStatus(Long userId, Integer deleted, Long adminId);

    /**
     * 添加社团经理
     */
    void addClubManager(Long clubId, Long userId, Long adminId);

    /**
     * 移除社团经理
     */
    void removeClubManager(Long clubId, Long userId, Long adminId);

    /**
     * 获取完整用户列表（包含敏感信息）
     */
    Page<UserInfoVO> getUserList(Long current, Long size, String keyword);

    // ==================== 活动与地点数据管理 ====================

    /**
     * 强制官方标记活动
     */
    void markEventOfficial(Long eventId, Boolean isOfficial, Long adminId);

    /**
     * 管理员删除活动
     */
    void deleteEvent(Long eventId, Long adminId);

    // ==================== 内容审核与合规 ====================

    /**
     * 管理员删除帖子
     */
    void deletePost(Long postId, Long adminId);

    /**
     * 管理员删除评论
     */
    void deleteComment(Long commentId, Long adminId);

    /**
     * 删除社团（包含级联删除）
     */
    void deleteClub(Long clubId, Long adminId);

    // ==================== 系统维护与监控 ====================

    /**
     * 清空Redis缓存
     */
    void clearCache(String cacheType);

    /**
     * 获取系统健康状态
     */
    Map<String, Object> getSystemHealth();
}
