package com.utm.what2do.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum BuildingCategory {
    ACADEMIC(1, "academic"),
    TECH(2, "tech"),
    SOCIAL(3, "social"),
    OUTDOOR(4, "outdoor"),
    OTHER(5, "other");

    @EnumValue
    private final int code;
    private final String label;

    BuildingCategory(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static BuildingCategory fromCode(int code) {
        for (BuildingCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown building category code: " + code);
    }
}
