package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View of a post-tag relation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTagVO {

    private Long id;
    private Long postId;
    private Long tagId;
    private LocalDateTime createdAt;
}
