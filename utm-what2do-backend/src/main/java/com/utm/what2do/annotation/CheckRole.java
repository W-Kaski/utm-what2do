package com.utm.what2do.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 用于标记需要进行角色权限验证的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {

    /**
     * 需要的角色列表，满足其中一个即可
     */
    String[] value() default {};

    /**
     * 是否需要同时拥有所有角色
     * true: 需要同时拥有所有角色 (AND)
     * false: 只需拥有其中一个角色 (OR)
     */
    boolean requireAll() default false;
}
