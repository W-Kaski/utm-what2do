package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for creating comments under a post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDTO {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 200, message = "评论内容不能超过200字")
    private String content;
}
