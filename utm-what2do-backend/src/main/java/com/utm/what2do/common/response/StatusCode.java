package com.utm.what2do.common.response;

import lombok.Getter;

/**
 * 状态码枚举
 */
@Getter
public enum StatusCode {

    SUCCESS(200, "成功"),
    CREATED(201, "创建成功"),
    ACCEPTED(202, "已接受"),
    NO_CONTENT(204, "无内容"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),

    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),

    // 业务错误码 (6000+)
    USER_NOT_FOUND(6001, "用户不存在"),
    USER_ALREADY_EXISTS(6002, "用户已存在"),
    PASSWORD_ERROR(6003, "密码错误"),
    INVALID_TOKEN(6004, "无效的Token"),
    TOKEN_EXPIRED(6005, "Token已过期"),

    EVENT_NOT_FOUND(6101, "活动不存在"),
    EVENT_ALREADY_ENDED(6102, "活动已结束"),
    EVENT_CANCELLED(6103, "活动已取消"),

    CLUB_NOT_FOUND(6201, "社团不存在"),
    NOT_CLUB_MEMBER(6202, "不是社团成员"),
    ALREADY_FOLLOWED(6203, "已关注该社团"),

    POST_NOT_FOUND(6301, "帖子不存在"),
    COMMENT_NOT_FOUND(6302, "评论不存在"),

    BUILDING_NOT_FOUND(6401, "建筑不存在");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
