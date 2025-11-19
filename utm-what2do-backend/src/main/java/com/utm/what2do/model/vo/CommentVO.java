package com.utm.what2do.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 评论VO
 */
@Data
public class CommentVO {

    private Long id;

    private Long postId;

    /**
     * 评论用户信息
     */
    private Long userId;
    private String username;
    private String avatarUrl;

    private String content;

    private Long parentCommentId;

    private Integer likesCount;

    private Date createdAt;

    /**
     * 子评论列表（回复）
     */
    private List<CommentVO> replies;
}
