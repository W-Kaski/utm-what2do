package com.utm.what2do.common.exception;

import com.utm.what2do.common.response.StatusCode;
import lombok.Getter;

/**
 * 业务异常基类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 使用状态码枚举构造异常
     */
    public BusinessException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    /**
     * 使用自定义状态码和消息构造异常
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用自定义消息构造异常（默认500状态码）
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }
}
