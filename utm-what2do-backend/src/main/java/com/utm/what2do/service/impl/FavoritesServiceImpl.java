package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.FavoritesMapper;
import com.utm.what2do.model.entity.*;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.model.vo.PostVO;
import com.utm.what2do.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 收藏Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites>
    implements FavoritesService {

    private final PostsService postsService;
    private final EventsService eventsService;
    private final UsersService usersService;

    private static final String TYPE_POST = "POST";
    private static final String TYPE_EVENT = "EVENT";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favoritePost(Long userId, Long postId) {
        // 验证帖子存在
        Posts post = postsService.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND);
        }

        // 检查是否已收藏
        if (isFavoritePost(userId, postId)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "已收藏该帖子");
        }

        // 创建收藏
        Favorites favorite = new Favorites();
        favorite.setUser_id(userId);
        favorite.setTarget_type(TYPE_POST);
        favorite.setTarget_id(postId);
        favorite.setCreated_at(new Date());

        this.save(favorite);

        // 更新用户收藏数
        updateUserFavoriteCount(userId, 1);

        log.info("收藏帖子成功: userId={}, postId={}", userId, postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavoritePost(Long userId, Long postId) {
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId)
               .eq(Favorites::getTarget_type, TYPE_POST)
               .eq(Favorites::getTarget_id, postId);

        Favorites favorite = this.getOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "未收藏该帖子");
        }

        this.removeById(favorite.getId());
        updateUserFavoriteCount(userId, -1);

        log.info("取消收藏帖子成功: userId={}, postId={}", userId, postId);
    }

    @Override
    public boolean isFavoritePost(Long userId, Long postId) {
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId)
               .eq(Favorites::getTarget_type, TYPE_POST)
               .eq(Favorites::getTarget_id, postId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<PostVO> getFavoritePosts(Long userId, Long current, Long size) {
        Page<Favorites> page = new Page<>(current, size);
        LambdaQueryWrapper<Favorites> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorites::getUser_id, userId)
               .eq(Favorites::getTarget_type, TYPE_POST)
               .orderByDesc(Favorites::getCreated_at);

        Page<Favorites> favoritePage = this.page(page, wrapper);

        Page<PostVO> voPage = new Page<>(current, size);
        voPage.setTotal(favoritePage.getTotal());
        voPage.setRecords(favoritePage.getRecords().stream()
            .map(fav -> postsService.getPostDetail(fav.getTarget_id()))
            .filter(vo -> vo != null)
            .collect(Collectors.toList()));

        return voPage;
    }

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
                // 简单转换，实际可能需要更复杂的逻辑
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
