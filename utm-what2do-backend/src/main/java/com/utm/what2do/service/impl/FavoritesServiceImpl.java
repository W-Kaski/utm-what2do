package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.FavoritesMapper;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.entity.Favorites;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.service.EventsService;
import com.utm.what2do.service.FavoritesService;
import com.utm.what2do.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 收藏Service实现（只支持收藏活动）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites>
    implements FavoritesService {

    private final EventsService eventsService;
    private final UsersService usersService;

    private static final String TYPE_EVENT = "EVENT";

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
            throw new BusinessException(StatusCode.PARAMS_ERROR, "已收藏该活动");
        }

        // 创建收藏
        Favorites favorite = new Favorites();
        favorite.setUser_id(userId);
        favorite.setTarget_type(TYPE_EVENT);
        favorite.setTarget_id(eventId);
        favorite.setCreated_at(new Date());

        this.save(favorite);
        updateUserFavoriteCount(userId, 1);

        log.info("收藏活动成功: userId={}, eventId={}", userId, eventId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavoriteEvent(Long userId, Long eventId) {
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId)
               .eq(Favorites::getTarget_type, TYPE_EVENT)
               .eq(Favorites::getTarget_id, eventId);

        Favorites favorite = this.getOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "未收藏该活动");
        }

        this.removeById(favorite.getId());
        updateUserFavoriteCount(userId, -1);

        log.info("取消收藏活动成功: userId={}, eventId={}", userId, eventId);
    }

    @Override
    public boolean isFavoriteEvent(Long userId, Long eventId) {
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId)
               .eq(Favorites::getTarget_type, TYPE_EVENT)
               .eq(Favorites::getTarget_id, eventId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<EventCardVO> getFavoriteEvents(Long userId, Long current, Long size) {
        Page<Favorites> page = new Page<>(current, size);
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId)
               .eq(Favorites::getTarget_type, TYPE_EVENT)
               .orderByDesc(Favorites::getCreated_at);

        Page<Favorites> favoritePage = this.page(page, wrapper);

        Page<EventCardVO> voPage = new Page<>(current, size);
        voPage.setTotal(favoritePage.getTotal());
        voPage.setRecords(favoritePage.getRecords().stream()
            .map(fav -> {
                Events event = eventsService.getById(fav.getTarget_id());
                if (event == null || event.getDeleted() == 1) return null;
                EventCardVO vo = new EventCardVO();
                vo.setId(event.getId());
                vo.setTitle(event.getTitle());
                vo.setSlug(event.getSlug());
                vo.setCoverUrl(event.getCover_url());
                vo.setStartTime(event.getStart_time());
                vo.setEndTime(event.getEnd_time());
                return vo;
            })
            .filter(vo -> vo != null)
            .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Long getFavoriteCount(Long userId) {
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId);
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
