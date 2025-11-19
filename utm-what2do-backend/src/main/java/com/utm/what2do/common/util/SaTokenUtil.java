package com.utm.what2do.common.util;

import cn.dev33.satoken.stp.StpUtil;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;

import java.util.List;

/**
 * Sa-Token 常用方法封装
 */
public class SaTokenUtil {

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static Long getLoginUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            throw new BusinessException(StatusCode.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前登录用户ID（字符串）
     * @return 用户ID字符串
     */
    public static String getLoginUserIdStr() {
        try {
            return StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            throw new BusinessException(StatusCode.UNAUTHORIZED);
        }
    }

    /**
     * 检查是否已登录
     * @return true-已登录，false-未登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取当前用户的角色列表
     * @return 角色列表
     */
    public static List<String> getRoleList() {
        return StpUtil.getRoleList();
    }

    /**
     * 检查当前用户是否拥有指定角色
     * @param role 角色标识
     * @return true-拥有，false-不拥有
     */
    public static boolean hasRole(String role) {
        return StpUtil.hasRole(role);
    }

    /**
     * 检查当前用户是否拥有任意一个角色
     * @param roles 角色列表
     * @return true-拥有任意一个，false-一个都没有
     */
    public static boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (StpUtil.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否拥有所有角色
     * @param roles 角色列表
     * @return true-全部拥有，false-至少缺少一个
     */
    public static boolean hasAllRoles(String... roles) {
        for (String role : roles) {
            if (!StpUtil.hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 为用户设置角色（通过Session）
     * @param userId 用户ID
     * @param role 角色标识
     */
    public static void setRole(Long userId, String role) {
        StpUtil.getSessionByLoginId(userId).set("role", role);
    }

    /**
     * 用户登录
     * @param userId 用户ID
     */
    public static void login(Long userId) {
        StpUtil.login(userId);
    }

    /**
     * 用户登录（带角色）
     * @param userId 用户ID
     * @param role 角色
     */
    public static void loginWithRole(Long userId, String role) {
        StpUtil.login(userId);
        // 注意：实际角色信息应该从数据库读取，这里只是示例
    }

    /**
     * 用户登出
     */
    public static void logout() {
        StpUtil.logout();
    }

    /**
     * 获取Token值
     * @return Token值
     */
    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }

    /**
     * 获取Token信息
     * @return Token信息
     */
    public static Object getTokenInfo() {
        return StpUtil.getTokenInfo();
    }
}
