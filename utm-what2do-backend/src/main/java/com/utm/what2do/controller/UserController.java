package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.dto.UserLoginDTO;
import com.utm.what2do.model.dto.UserRegisterDTO;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户注册、登录、个人档案等API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "创建新用户账户")
    @PostMapping("/register")
    public ResultVO<UserInfoVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        UserInfoVO userInfo = usersService.register(dto);
        return ResultVO.success("注册成功", userInfo);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "验证用户身份并获取Token")
    @PostMapping("/login")
    public ResultVO<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        Map<String, Object> result = usersService.login(dto);
        return ResultVO.success("登录成功", result);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "销毁用户的Token会话")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/logout")
    public ResultVO<Void> logout() {
        StpUtil.logout();
        return ResultVO.success("登出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取个人档案", description = "获取当前登录用户的详细信息")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/me")
    public ResultVO<UserInfoVO> getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserInfoVO userInfo = usersService.getUserInfo(userId);
        return ResultVO.success(userInfo);
    }

    /**
     * 获取指定用户信息
     */
    @Operation(summary = "获取用户档案", description = "获取任一用户的公开档案信息")
    @GetMapping("/{id}")
    public ResultVO<UserInfoVO> getUserById(@PathVariable Long id) {
        UserInfoVO userInfo = usersService.getUserInfo(id);
        return ResultVO.success(userInfo);
    }

    /**
     * 更新个人档案
     */
    @Operation(summary = "更新个人档案", description = "更新用户的显示名称、头像、简介等")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PutMapping("/me")
    public ResultVO<UserInfoVO> updateProfile(@RequestBody UserInfoVO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        UserInfoVO updated = usersService.updateProfile(userId, dto);
        return ResultVO.success("更新成功", updated);
    }

    /**
     * 关注社团
     */
    @Operation(summary = "关注社团", description = "当前用户关注指定社团")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/follow/club/{id}")
    public ResultVO<Void> followClub(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        usersService.followClub(userId, id);
        return ResultVO.success("关注成功");
    }

    /**
     * 取消关注社团
     */
    @Operation(summary = "取消关注社团", description = "当前用户取消关注指定社团")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/follow/club/{id}")
    public ResultVO<Void> unfollowClub(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        usersService.unfollowClub(userId, id);
        return ResultVO.success("取消关注成功");
    }
}
