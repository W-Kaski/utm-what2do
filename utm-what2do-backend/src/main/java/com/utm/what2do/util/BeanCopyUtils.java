package com.utm.what2do.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static <S, T> T copy(S source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <S, T> T copy(S source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to copy bean", e);
        }
    }

    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(item -> copy(item, targetClass))
                .collect(Collectors.toList());
    }
}
