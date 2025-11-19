package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.entity.Favorites;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.model.vo.PostVO;

/**
 * 收藏Service
 */
public interface FavoritesService extends IService<Favorites> {

    /**
     * 收藏帖子
     */
    void favoritePost(Long userId, Long postId);

    /**
     * 取消收藏帖子
     */
    void unfavoritePost(Long userId, Long postId);

    /**
     * 检查是否已收藏帖子
     */
    boolean isFavoritePost(Long userId, Long postId);

    /**
     * 获取收藏的帖子列表
     */
    Page<PostVO> getFavoritePosts(Long userId, Long current, Long size);

    /**
     * 收藏活动
     */
    void favoriteEvent(Long userId, Long eventId);

    /**
     * 取消收藏活动
     */
    void unfavoriteEvent(Long userId, Long eventId);

    /**
     * 检查是否已收藏活动
     */
    boolean isFavoriteEvent(Long userId, Long eventId);

    /**
     * 获取收藏的活动列表
     */
    Page<EventCardVO> getFavoriteEvents(Long userId, Long current, Long size);

    /**
     * 获取用户收藏总数
     */
    Long getFavoriteCount(Long userId);
}
