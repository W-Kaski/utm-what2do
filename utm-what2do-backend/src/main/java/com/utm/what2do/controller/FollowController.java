package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.vo.ClubDetailVO;
import com.utm.what2do.service.FollowsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 关注管理Controller（关注社团）
 */
@Tag(name = "关注管理", description = "社团关注、取消关注、关注列表等API")
@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowsService followsService;

    /**
     * 关注社团
     */
    @Operation(summary = "关注社团", description = "关注指定社团")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/clubs/{clubId}")
    public ResultVO<Void> followClub(@PathVariable Long clubId) {
        Long userId = StpUtil.getLoginIdAsLong();
        followsService.followClub(userId, clubId);
        return ResultVO.success("关注成功");
    }

    /**
     * 取消关注社团
     */
    @Operation(summary = "取消关注社团", description = "取消关注指定社团")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/clubs/{clubId}")
    public ResultVO<Void> unfollowClub(@PathVariable Long clubId) {
        Long userId = StpUtil.getLoginIdAsLong();
        followsService.unfollowClub(userId, clubId);
        return ResultVO.success("取消关注成功");
    }

    /**
     * 检查是否已关注社团
     */
    @Operation(summary = "检查社团关注状态", description = "检查当前用户是否已关注目标社团")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/clubs/{clubId}/check")
    public ResultVO<Map<String, Boolean>> checkFollowing(@PathVariable Long clubId) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean isFollowing = followsService.isFollowingClub(userId, clubId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        return ResultVO.success(result);
    }

    /**
     * 获取我关注的社团列表
     */
    @Operation(summary = "获取关注的社团", description = "获取当前用户关注的社团列表")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/clubs")
    public ResultVO<Page<ClubDetailVO>> getFollowingClubs(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<ClubDetailVO> page = followsService.getFollowingClubs(userId, current, size);
        return ResultVO.success(page);
    }

    /**
     * 获取关注统计
     */
    @Operation(summary = "获取关注统计", description = "获取当前用户关注的社团数")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/count")
    public ResultVO<Map<String, Long>> getFollowCount() {
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Long> stats = new HashMap<>();
        stats.put("followingCount", followsService.getFollowingCount(userId));
        return ResultVO.success(stats);
    }

    /**
     * 获取社团粉丝数
     */
    @Operation(summary = "获取社团粉丝数", description = "获取指定社团的粉丝数量")
    @GetMapping("/clubs/{clubId}/followers/count")
    public ResultVO<Map<String, Long>> getClubFollowerCount(@PathVariable Long clubId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("followerCount", followsService.getClubFollowerCount(clubId));
        return ResultVO.success(stats);
    }
}
