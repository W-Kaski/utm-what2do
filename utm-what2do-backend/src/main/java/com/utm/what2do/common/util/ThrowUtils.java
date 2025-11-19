package com.utm.what2do.common.util;

import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;

/**
 * 异常抛出工具类
 * 简化异常抛出代码
 */
public class ThrowUtils {

    /**
     * 条件为true时抛出异常
     */
    public static void throwIf(boolean condition, StatusCode statusCode) {
        if (condition) {
            throw new BusinessException(statusCode);
        }
    }

    /**
     * 条件为true时抛出异常（自定义消息）
     */
    public static void throwIf(boolean condition, Integer code, String message) {
        if (condition) {
            throw new BusinessException(code, message);
        }
    }

    /**
     * 条件为true时抛出异常（默认500状态码）
     */
    public static void throwIf(boolean condition, String message) {
        if (condition) {
            throw new BusinessException(message);
        }
    }

    /**
     * 抛出业务异常
     */
    public static void throwException(StatusCode statusCode) {
        throw new BusinessException(statusCode);
    }

    /**
     * 抛出业务异常（自定义消息）
     */
    public static void throwException(Integer code, String message) {
        throw new BusinessException(code, message);
    }

    /**
     * 抛出业务异常（默认500状态码）
     */
    public static void throwException(String message) {
        throw new BusinessException(message);
    }
}
