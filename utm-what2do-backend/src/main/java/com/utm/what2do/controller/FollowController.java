package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.FollowsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 关注管理Controller
 */
@Tag(name = "关注管理", description = "用户关注、取消关注、关注列表等API")
@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowsService followsService;

    /**
     * 关注用户
     */
    @Operation(summary = "关注用户", description = "关注指定用户")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/{targetUserId}")
    public ResultVO<Void> followUser(@PathVariable Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        followsService.followUser(userId, targetUserId);
        return ResultVO.success("关注成功");
    }

    /**
     * 取消关注
     */
    @Operation(summary = "取消关注", description = "取消关注指定用户")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/{targetUserId}")
    public ResultVO<Void> unfollowUser(@PathVariable Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        followsService.unfollowUser(userId, targetUserId);
        return ResultVO.success("取消关注成功");
    }

    /**
     * 检查是否已关注
     */
    @Operation(summary = "检查关注状态", description = "检查当前用户是否已关注目标用户")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/check/{targetUserId}")
    public ResultVO<Map<String, Boolean>> checkFollowing(@PathVariable Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean isFollowing = followsService.isFollowing(userId, targetUserId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        return ResultVO.success(result);
    }

    /**
     * 获取我的关注列表
     */
    @Operation(summary = "获取关注列表", description = "获取当前用户关注的人")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/following")
    public ResultVO<Page<UserInfoVO>> getMyFollowing(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<UserInfoVO> page = followsService.getFollowingList(userId, current, size);
        return ResultVO.success(page);
    }

    /**
     * 获取我的粉丝列表
     */
    @Operation(summary = "获取粉丝列表", description = "获取关注当前用户的人")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/followers")
    public ResultVO<Page<UserInfoVO>> getMyFollowers(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<UserInfoVO> page = followsService.getFollowerList(userId, current, size);
        return ResultVO.success(page);
    }

    /**
     * 获取指定用户的关注列表
     */
    @Operation(summary = "获取用户关注列表", description = "获取指定用户关注的人")
    @GetMapping("/user/{userId}/following")
    public ResultVO<Page<UserInfoVO>> getUserFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<UserInfoVO> page = followsService.getFollowingList(userId, current, size);
        return ResultVO.success(page);
    }

    /**
     * 获取指定用户的粉丝列表
     */
    @Operation(summary = "获取用户粉丝列表", description = "获取关注指定用户的人")
    @GetMapping("/user/{userId}/followers")
    public ResultVO<Page<UserInfoVO>> getUserFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<UserInfoVO> page = followsService.getFollowerList(userId, current, size);
        return ResultVO.success(page);
    }

    /**
     * 获取关注统计
     */
    @Operation(summary = "获取关注统计", description = "获取指定用户的关注数和粉丝数")
    @GetMapping("/stats/{userId}")
    public ResultVO<Map<String, Long>> getFollowStats(@PathVariable Long userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("followingCount", followsService.getFollowingCount(userId));
        stats.put("followerCount", followsService.getFollowerCount(userId));
        return ResultVO.success(stats);
    }
}
