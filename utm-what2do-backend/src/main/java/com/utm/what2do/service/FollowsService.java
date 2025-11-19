package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.entity.Follows;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.UserInfoVO;

/**
 * 关注Service
 */
public interface FollowsService extends IService<Follows> {

    /**
     * 关注用户
     */
    void followUser(Long followerId, Long targetUserId);

    /**
     * 取消关注用户
     */
    void unfollowUser(Long followerId, Long targetUserId);

    /**
     * 检查是否已关注
     */
    boolean isFollowing(Long followerId, Long targetUserId);

    /**
     * 获取关注列表（我关注的人）
     */
    Page<UserInfoVO> getFollowingList(Long userId, Long current, Long size);

    /**
     * 获取粉丝列表（关注我的人）
     */
    Page<UserInfoVO> getFollowerList(Long userId, Long current, Long size);

    /**
     * 获取关注数
     */
    Long getFollowingCount(Long userId);

    /**
     * 获取粉丝数
     */
    Long getFollowerCount(Long userId);
}
