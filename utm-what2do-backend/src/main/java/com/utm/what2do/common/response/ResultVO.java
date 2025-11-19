package com.utm.what2do.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API返回结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功返回（带数据）
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(
                StatusCode.SUCCESS.getCode(),
                StatusCode.SUCCESS.getMessage(),
                data,
                System.currentTimeMillis()
        );
    }

    /**
     * 成功返回（带自定义消息和数据）
     */
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(
                StatusCode.SUCCESS.getCode(),
                message,
                data,
                System.currentTimeMillis()
        );
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> ResultVO<T> success() {
        return new ResultVO<>(
                StatusCode.SUCCESS.getCode(),
                StatusCode.SUCCESS.getMessage(),
                null,
                System.currentTimeMillis()
        );
    }

    /**
     * 成功返回（带自定义消息）
     */
    public static <T> ResultVO<T> success(String message) {
        return new ResultVO<>(
                StatusCode.SUCCESS.getCode(),
                message,
                null,
                System.currentTimeMillis()
        );
    }

    /**
     * 失败返回（使用状态码枚举）
     */
    public static <T> ResultVO<T> error(StatusCode statusCode) {
        return new ResultVO<>(
                statusCode.getCode(),
                statusCode.getMessage(),
                null,
                System.currentTimeMillis()
        );
    }

    /**
     * 失败返回（自定义状态码和消息）
     */
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(
                code,
                message,
                null,
                System.currentTimeMillis()
        );
    }

    /**
     * 失败返回（自定义消息，使用默认500状态码）
     */
    public static <T> ResultVO<T> error(String message) {
        return new ResultVO<>(
                StatusCode.INTERNAL_SERVER_ERROR.getCode(),
                message,
                null,
                System.currentTimeMillis()
        );
    }
}
