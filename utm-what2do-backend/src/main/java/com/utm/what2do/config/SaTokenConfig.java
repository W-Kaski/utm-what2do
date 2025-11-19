package com.utm.what2do.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.utm.what2do.constant.RoleConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 配置类
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 过滤器
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定拦截路由
                .addInclude("/**")
                // 指定放行路由
                .addExclude(
                        "/favicon.ico",
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**",
                        "/api/v1/users/register",
                        "/api/v1/users/login",
                        "/api/v1/events",
                        "/api/v1/events/**",
                        "/api/v1/buildings",
                        "/api/v1/buildings/**",
                        "/api/v1/clubs",
                        "/api/v1/clubs/**",
                        "/api/v1/posts",
                        "/api/v1/posts/**",
                        "/api/v1/tags/**"
                )
                // 认证函数: 每次请求都会执行
                .setAuth(obj -> {
                    SaRouter.match("/**")
                            // 排除OPTIONS请求（CORS预检）
                            .notMatch(SaHttpMethod.OPTIONS)
                            .check(r -> {
                                // 这里可以添加全局的认证逻辑
                                // 具体的权限校验在Controller层通过 @CheckRole 注解实现
                            });
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    return "{\"code\":401,\"message\":\"" + e.getMessage() + "\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}";
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    // 设置跨域响应头
                    SaHolder.getResponse()
                            .setHeader("Access-Control-Allow-Origin", "*")
                            .setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                            .setHeader("Access-Control-Allow-Headers", "*")
                            .setHeader("Access-Control-Max-Age", "3600");
                });
    }

    /**
     * 自定义权限验证接口扩展
     */
    @Bean
    public StpInterface stpInterface() {
        return new StpInterface() {
            /**
             * 返回一个账号所拥有的权限码集合
             */
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                // 这里可以从数据库查询用户的权限列表
                // 目前返回空列表，权限主要通过角色控制
                return new ArrayList<>();
            }

            /**
             * 返回一个账号所拥有的角色标识集合
             */
            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                // 这里应该从数据库查询用户的角色
                // 暂时返回默认角色，实际使用时需要从Service层获取
                List<String> roleList = new ArrayList<>();

                // 从Session中获取用户角色（在登录时设置）
                Object role = StpUtil.getSession().get("role");
                if (role != null) {
                    roleList.add(role.toString());
                } else {
                    // 默认角色
                    roleList.add(RoleConstants.USER);
                }

                return roleList;
            }
        };
    }
}
