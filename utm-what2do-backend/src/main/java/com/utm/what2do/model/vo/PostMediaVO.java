package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object for media items attached to posts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMediaVO {

    private Long id;
    private Long postId;
    private String type;
    private String url;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}
