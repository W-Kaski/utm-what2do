package com.utm.what2do.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class R<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) {
        return R.<T>builder().code(200).message("success").data(data).build();
    }

    public static R<Void> ok() {
        return R.<Void>builder().code(200).message("success").build();
    }

    public static <T> R<T> fail(String message) {
        return R.<T>builder().code(500).message(message).build();
    }
}
