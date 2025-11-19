package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.entity.Follows;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.ClubDetailVO;

/**
 * 关注Service（关注社团）
 */
public interface FollowsService extends IService<Follows> {

    /**
     * 关注社团
     */
    void followClub(Long userId, Long clubId);

    /**
     * 取消关注社团
     */
    void unfollowClub(Long userId, Long clubId);

    /**
     * 检查是否已关注社团
     */
    boolean isFollowingClub(Long userId, Long clubId);

    /**
     * 获取用户关注的社团列表
     */
    Page<ClubDetailVO> getFollowingClubs(Long userId, Long current, Long size);

    /**
     * 获取用户关注的社团数
     */
    Long getFollowingCount(Long userId);

    /**
     * 获取社团的粉丝数
     */
    Long getClubFollowerCount(Long clubId);
}
