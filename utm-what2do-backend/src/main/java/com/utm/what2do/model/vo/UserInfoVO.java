package com.utm.what2do.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息展示VO
 */
@Data
public class UserInfoVO {

    private Long id;

    private String username;

    private String email;

    private String displayName;

    private String avatar;

    private String bio;

    private String role;

    private Integer followingCount;

    private Integer favoritesCount;

    private LocalDateTime createdAt;
}
