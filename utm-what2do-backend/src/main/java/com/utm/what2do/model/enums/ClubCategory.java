package com.utm.what2do.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ClubCategory {
    ACADEMIC(1, "academic"),
    ARTS(2, "arts"),
    SPORTS(3, "sports"),
    CULTURE(4, "culture"),
    COMMUNITY(5, "community"),
    INTERESTS(6, "interests"),
    CAREER(7, "career");

    @EnumValue
    private final int code;
    private final String label;

    ClubCategory(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ClubCategory fromCode(int code) {
        for (ClubCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown club category code: " + code);
    }
}
