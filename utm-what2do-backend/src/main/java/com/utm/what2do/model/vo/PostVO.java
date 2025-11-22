package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object mirroring the posts table structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostVO {

    private Long id;
    private Long authorUserId;
    private Long authorClubId;
    private String authorName;
    private String authorAvatar;
    private String content;
    private Boolean pinned;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer repostsCount;
    private List<String> mediaUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
