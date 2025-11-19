package com.utm.what2do.aop;

import cn.dev33.satoken.stp.StpUtil;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 权限校验切面
 * 拦截带有 @CheckRole 注解的方法，进行 Sa-Token 权限验证
 */
@Aspect
@Component
@Slf4j
public class AuthCheckAspect {

    /**
     * 环绕通知：对带有 @CheckRole 注解的方法进行权限校验
     */
    @Around("@annotation(checkRole)")
    public Object doAuthCheck(ProceedingJoinPoint joinPoint, CheckRole checkRole) throws Throwable {
        String[] roles = checkRole.value();

        // 如果没有指定角色，直接通过
        if (roles.length == 0) {
            return joinPoint.proceed();
        }

        // 检查用户是否登录
        if (!StpUtil.isLogin()) {
            log.warn("用户未登录，访问被拒绝: {}", joinPoint.getSignature());
            throw new BusinessException(401, "请先登录");
        }

        // 获取当前用户的角色
        Object loginId = StpUtil.getLoginId();
        log.debug("用户 {} 尝试访问需要角色 {} 的接口", loginId, String.join(",", roles));

        try {
            if (checkRole.requireAll()) {
                // 需要同时拥有所有角色
                for (String role : roles) {
                    if (!StpUtil.hasRole(role)) {
                        log.warn("用户 {} 缺少必需角色: {}", loginId, role);
                        throw new BusinessException(403, "权限不足，需要角色: " + String.join(" 和 ", roles));
                    }
                }
            } else {
                // 只需拥有其中一个角色
                boolean hasAnyRole = false;
                for (String role : roles) {
                    if (StpUtil.hasRole(role)) {
                        hasAnyRole = true;
                        break;
                    }
                }
                if (!hasAnyRole) {
                    log.warn("用户 {} 没有所需的任何角色: {}", loginId, String.join(",", roles));
                    throw new BusinessException(403, "权限不足，需要角色: " + String.join(" 或 ", roles));
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("权限校验异常", e);
            throw new BusinessException(500, "权限校验失败");
        }

        // 权限验证通过，继续执行
        return joinPoint.proceed();
    }
}
