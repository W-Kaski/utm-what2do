package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论创建DTO
 */
@Data
public class CommentCreateDTO {

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000字")
    private String content;

    /**
     * 父评论ID，用于回复评论
     */
    private Long parentCommentId;
}
