package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for creating or updating comments under a post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDTO {

    private Long id;

    @NotNull
    private Long postId;

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 200)
    private String content;

    private Long parentCommentId;
}
