package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.EventFavoritesMapper;
import com.utm.what2do.model.entity.EventFavorites;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.service.EventsService;
import com.utm.what2do.service.FavoritesService;
import com.utm.what2do.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 活动收藏Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl extends ServiceImpl<EventFavoritesMapper, EventFavorites>
    implements FavoritesService {

    private final EventsService eventsService;
    private final UsersService usersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favoriteEvent(Long userId, Long eventId) {
        // 验证活动存在
        Events event = eventsService.getById(eventId);
        if (event == null || event.getDeleted() == 1) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 检查是否已收藏
        if (isFavoriteEvent(userId, eventId)) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "已收藏该活动");
        }

        // 创建收藏
        EventFavorites favorite = new EventFavorites();
        favorite.setUser_id(userId);
        favorite.setEvent_id(eventId);
        favorite.setCreated_at(new Date());

        this.save(favorite);
        updateUserFavoriteCount(userId, 1);

        log.info("收藏活动成功: userId={}, eventId={}", userId, eventId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavoriteEvent(Long userId, Long eventId) {
        LambdaQueryWrapper<EventFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventFavorites::getUser_id, userId)
               .eq(EventFavorites::getEvent_id, eventId);

        EventFavorites favorite = this.getOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "未收藏该活动");
        }

        this.removeById(favorite.getId());
        updateUserFavoriteCount(userId, -1);

        log.info("取消收藏活动成功: userId={}, eventId={}", userId, eventId);
    }

    @Override
    public boolean isFavoriteEvent(Long userId, Long eventId) {
        LambdaQueryWrapper<EventFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventFavorites::getUser_id, userId)
               .eq(EventFavorites::getEvent_id, eventId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<EventCardVO> getFavoriteEvents(Long userId, Long current, Long size) {
        Page<EventFavorites> page = new Page<>(current, size);
        LambdaQueryWrapper<EventFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventFavorites::getUser_id, userId)
               .orderByDesc(EventFavorites::getCreated_at);

        Page<EventFavorites> favoritePage = this.page(page, wrapper);

        Page<EventCardVO> voPage = new Page<>(current, size);
        voPage.setTotal(favoritePage.getTotal());
        voPage.setRecords(favoritePage.getRecords().stream()
            .map(fav -> {
                Events event = eventsService.getById(fav.getEvent_id());
                if (event == null || event.getDeleted() == 1) return null;
                EventCardVO vo = new EventCardVO();
                vo.setId(event.getId());
                vo.setTitle(event.getTitle());
                vo.setSlug(event.getSlug());
                // Convert Date to LocalDateTime
                if (event.getStart_time() != null) {
                    vo.setStartTime(event.getStart_time().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
                if (event.getEnd_time() != null) {
                    vo.setEndTime(event.getEnd_time().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
                return vo;
            })
            .filter(vo -> vo != null)
            .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Long getFavoriteCount(Long userId) {
        LambdaQueryWrapper<EventFavorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventFavorites::getUser_id, userId);
        return this.count(wrapper);
    }

    private void updateUserFavoriteCount(Long userId, int delta) {
        Users user = usersService.getById(userId);
        if (user != null) {
            int newCount = Math.max(0, user.getFavorites_count() + delta);
            user.setFavorites_count(newCount);
            usersService.updateById(user);
        }
    }
}
