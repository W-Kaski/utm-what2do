package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.entity.EventFavorites;
import com.utm.what2do.model.vo.EventCardVO;

/**
 * 活动收藏Service
 */
public interface FavoritesService extends IService<EventFavorites> {

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
