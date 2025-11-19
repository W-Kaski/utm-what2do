package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.dto.CommentCreateDTO;
import com.utm.what2do.model.entity.PostComments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.CommentVO;

import java.util.List;

/**
 * 帖子评论Service
 */
public interface PostCommentsService extends IService<PostComments> {

    /**
     * 创建评论
     */
    CommentVO createComment(CommentCreateDTO dto, Long userId);

    /**
     * 获取帖子的评论列表（分页）
     */
    Page<CommentVO> getCommentsByPost(Long postId, Long current, Long size);

    /**
     * 获取评论详情
     */
    CommentVO getCommentDetail(Long commentId);

    /**
     * 删除评论（软删除）
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 点赞评论
     */
    void likeComment(Long commentId);

    /**
     * 获取评论的回复列表
     */
    List<CommentVO> getReplies(Long commentId);
}
