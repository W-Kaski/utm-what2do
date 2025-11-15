package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View friendly representation of a user profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;
    private String email;
    private String username;
    private String displayName;
    private String role;
    private String avatarUrl;
    private String coverUrl;
    private String bio;
    private Integer followingCount;
    private Integer favoritesCount;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
