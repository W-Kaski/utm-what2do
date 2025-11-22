package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object mirroring the post_comments table structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String authorName;
    private String authorAvatar;
    private String content;
    private Long parentCommentId;
    private Integer likesCount;
    private Boolean reviewed;
    private LocalDateTime createdAt;
}
