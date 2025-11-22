package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.dto.admin.ClubManagerDTO;
import com.utm.what2do.model.dto.admin.EventOfficialDTO;
import com.utm.what2do.model.dto.admin.UserRoleDTO;
import com.utm.what2do.model.dto.admin.UserStatusDTO;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员模块Controller
 */
@Tag(name = "管理员模块", description = "用户管理、内容审核、系统维护等管理功能")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==================== 用户与社团关系管理 ====================

    /**
     * 修改用户角色
     */
    @Operation(summary = "修改用户角色", description = "将用户提升/降级为 USER, CLUB_MANAGER 或 ADMIN")
    @CheckRole(RoleConstants.ADMIN)
    @PutMapping("/users/{id}/role")
    public ResultVO<Void> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleDTO dto) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.updateUserRole(id, dto.getRole(), adminId);
        return ResultVO.success("用户角色更新成功");
    }

    /**
     * 禁用/解封用户
     */
    @Operation(summary = "禁用/解封用户", description = "设置用户的禁用状态")
    @CheckRole(RoleConstants.ADMIN)
    @PutMapping("/users/{id}/status")
    public ResultVO<Void> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusDTO dto) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.updateUserStatus(id, dto.getDeleted(), adminId);
        return ResultVO.success(dto.getDeleted() == 1 ? "用户已禁用" : "用户已解封");
    }

    /**
     * 添加社团经理
     */
    @Operation(summary = "添加社团经理", description = "指定用户为社团的 MANAGER")
    @CheckRole(RoleConstants.ADMIN)
    @PostMapping("/clubs/{clubId}/manager")
    public ResultVO<Void> addClubManager(
            @PathVariable Long clubId,
            @Valid @RequestBody ClubManagerDTO dto) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.addClubManager(clubId, dto.getUserId(), adminId);
        return ResultVO.success("社团经理添加成功");
    }

    /**
     * 移除社团经理
     */
    @Operation(summary = "移除社团经理", description = "将社团经理降级为普通成员")
    @CheckRole(RoleConstants.ADMIN)
    @DeleteMapping("/clubs/{clubId}/manager")
    public ResultVO<Void> removeClubManager(
            @PathVariable Long clubId,
            @Valid @RequestBody ClubManagerDTO dto) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.removeClubManager(clubId, dto.getUserId(), adminId);
        return ResultVO.success("社团经理已移除");
    }

    /**
     * 获取完整用户列表
     */
    @Operation(summary = "获取用户列表", description = "获取包含敏感信息的完整用户档案")
    @CheckRole(RoleConstants.ADMIN)
    @GetMapping("/users")
    public ResultVO<Page<UserInfoVO>> getUserList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        Page<UserInfoVO> page = adminService.getUserList(current, size, keyword);
        return ResultVO.success(page);
    }

    // ==================== 活动与地点数据管理 ====================

    /**
     * 强制官方标记活动
     */
    @Operation(summary = "标记活动官方状态", description = "将活动设为官方/非官方")
    @CheckRole(RoleConstants.ADMIN)
    @PutMapping("/events/{id}/official")
    public ResultVO<Void> markEventOfficial(
            @PathVariable Long id,
            @Valid @RequestBody EventOfficialDTO dto) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.markEventOfficial(id, dto.getIsOfficial(), adminId);
        return ResultVO.success(dto.getIsOfficial() ? "活动已标记为官方" : "活动已取消官方标记");
    }

    /**
     * 管理员删除活动
     */
    @Operation(summary = "删除活动", description = "强制删除任何活动")
    @CheckRole(RoleConstants.ADMIN)
    @DeleteMapping("/events/{id}")
    public ResultVO<Void> deleteEvent(@PathVariable Long id) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.deleteEvent(id, adminId);
        return ResultVO.success("活动删除成功");
    }

    // ==================== 内容审核与合规 ====================

    /**
     * 管理员删除帖子
     */
    @Operation(summary = "删除帖子", description = "强制删除任何帖子")
    @CheckRole(RoleConstants.ADMIN)
    @DeleteMapping("/posts/{id}")
    public ResultVO<Void> deletePost(@PathVariable Long id) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.deletePost(id, adminId);
        return ResultVO.success("帖子删除成功");
    }

    /**
     * 管理员删除评论
     */
    @Operation(summary = "删除评论", description = "强制删除任何评论")
    @CheckRole(RoleConstants.ADMIN)
    @DeleteMapping("/comments/{id}")
    public ResultVO<Void> deleteComment(@PathVariable Long id) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.deleteComment(id, adminId);
        return ResultVO.success("评论删除成功");
    }

    /**
     * 删除社团（包含级联删除）
     */
    @Operation(summary = "删除社团", description = "删除社团及其所有关联数据")
    @CheckRole(RoleConstants.ADMIN)
    @DeleteMapping("/clubs/{id}")
    public ResultVO<Void> deleteClub(@PathVariable Long id) {
        Long adminId = StpUtil.getLoginIdAsLong();
        adminService.deleteClub(id, adminId);
        return ResultVO.success("社团删除成功");
    }

    // ==================== 系统维护与监控 ====================

    /**
     * 清空Redis缓存
     */
    @Operation(summary = "清空缓存", description = "清空Redis缓存，支持按类型清除")
    @CheckRole(RoleConstants.ADMIN)
    @PostMapping("/cache/clear")
    public ResultVO<Void> clearCache(
            @RequestParam(required = false) String type) {
        adminService.clearCache(type);
        return ResultVO.success("缓存清除成功");
    }

    /**
     * 获取系统健康状态
     */
    @Operation(summary = "系统健康检查", description = "检查数据库、Redis连接状态和内存使用")
    @CheckRole(RoleConstants.ADMIN)
    @GetMapping("/health")
    public ResultVO<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = adminService.getSystemHealth();
        return ResultVO.success(health);
    }
}
