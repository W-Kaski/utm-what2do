package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.model.vo.PostVO;
import com.utm.what2do.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏管理Controller
 */
@Tag(name = "收藏管理", description = "帖子和活动的收藏、取消收藏等API")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoritesService favoritesService;

    // ========== 帖子收藏 ==========

    /**
     * 收藏帖子
     */
    @Operation(summary = "收藏帖子", description = "收藏指定帖子")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/posts/{postId}")
    public ResultVO<Void> favoritePost(@PathVariable Long postId) {
        Long userId = StpUtil.getLoginIdAsLong();
        favoritesService.favoritePost(userId, postId);
        return ResultVO.success("收藏成功");
    }

    /**
     * 取消收藏帖子
     */
    @Operation(summary = "取消收藏帖子", description = "取消收藏指定帖子")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/posts/{postId}")
    public ResultVO<Void> unfavoritePost(@PathVariable Long postId) {
        Long userId = StpUtil.getLoginIdAsLong();
        favoritesService.unfavoritePost(userId, postId);
        return ResultVO.success("取消收藏成功");
    }

    /**
     * 检查是否已收藏帖子
     */
    @Operation(summary = "检查帖子收藏状态", description = "检查当前用户是否已收藏指定帖子")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/posts/{postId}/check")
    public ResultVO<Map<String, Boolean>> checkPostFavorite(@PathVariable Long postId) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean isFavorite = favoritesService.isFavoritePost(userId, postId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        return ResultVO.success(result);
    }

    /**
     * 获取收藏的帖子列表
     */
    @Operation(summary = "获取收藏的帖子", description = "获取当前用户收藏的帖子列表")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/posts")
    public ResultVO<Page<PostVO>> getFavoritePosts(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<PostVO> page = favoritesService.getFavoritePosts(userId, current, size);
        return ResultVO.success(page);
    }

    // ========== 活动收藏 ==========

    /**
     * 收藏活动
     */
    @Operation(summary = "收藏活动", description = "收藏指定活动")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/events/{eventId}")
    public ResultVO<Void> favoriteEvent(@PathVariable Long eventId) {
        Long userId = StpUtil.getLoginIdAsLong();
        favoritesService.favoriteEvent(userId, eventId);
        return ResultVO.success("收藏成功");
    }

    /**
     * 取消收藏活动
     */
    @Operation(summary = "取消收藏活动", description = "取消收藏指定活动")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/events/{eventId}")
    public ResultVO<Void> unfavoriteEvent(@PathVariable Long eventId) {
        Long userId = StpUtil.getLoginIdAsLong();
        favoritesService.unfavoriteEvent(userId, eventId);
        return ResultVO.success("取消收藏成功");
    }

    /**
     * 检查是否已收藏活动
     */
    @Operation(summary = "检查活动收藏状态", description = "检查当前用户是否已收藏指定活动")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/events/{eventId}/check")
    public ResultVO<Map<String, Boolean>> checkEventFavorite(@PathVariable Long eventId) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean isFavorite = favoritesService.isFavoriteEvent(userId, eventId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        return ResultVO.success(result);
    }

    /**
     * 获取收藏的活动列表
     */
    @Operation(summary = "获取收藏的活动", description = "获取当前用户收藏的活动列表")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/events")
    public ResultVO<Page<EventCardVO>> getFavoriteEvents(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<EventCardVO> page = favoritesService.getFavoriteEvents(userId, current, size);
        return ResultVO.success(page);
    }

    // ========== 统计 ==========

    /**
     * 获取收藏统计
     */
    @Operation(summary = "获取收藏统计", description = "获取当前用户的收藏总数")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/count")
    public ResultVO<Map<String, Long>> getFavoriteCount() {
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Long> result = new HashMap<>();
        result.put("count", favoritesService.getFavoriteCount(userId));
        return ResultVO.success(result);
    }
}
