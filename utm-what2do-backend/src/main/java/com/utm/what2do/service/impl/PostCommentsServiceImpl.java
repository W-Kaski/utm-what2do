package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.PostCommentsMapper;
import com.utm.what2do.model.dto.CommentCreateDTO;
import com.utm.what2do.model.entity.PostComments;
import com.utm.what2do.model.entity.Posts;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.CommentVO;
import com.utm.what2do.service.PostCommentsService;
import com.utm.what2do.service.PostsService;
import com.utm.what2do.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子评论Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService {

    private final PostsService postsService;
    private final UsersService usersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(CommentCreateDTO dto, Long userId) {
        // 1. 验证帖子存在
        Posts post = postsService.getById(dto.getPostId());
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND);
        }

        // 2. 如果是回复，验证父评论存在
        if (dto.getParentCommentId() != null) {
            PostComments parentComment = this.getById(dto.getParentCommentId());
            if (parentComment == null || parentComment.getDeleted() == 1) {
                throw new BusinessException(StatusCode.COMMENT_NOT_FOUND);
            }
        }

        // 3. 创建评论
        PostComments comment = new PostComments();
        comment.setPost_id(dto.getPostId());
        comment.setUser_id(userId);
        comment.setContent(dto.getContent());
        comment.setParent_comment_id(dto.getParentCommentId());
        comment.setLikes_count(0);
        comment.setReviewed(1); // 默认通过审核
        comment.setCreated_at(new Date());
        comment.setDeleted(0);

        boolean saved = this.save(comment);
        if (!saved) {
            throw new BusinessException(500, "评论发布失败");
        }

        // 4. 更新帖子评论数
        post.setComments_count(post.getComments_count() + 1);
        postsService.updateById(post);

        log.info("评论发布成功: commentId={}, postId={}, userId={}",
            comment.getId(), dto.getPostId(), userId);

        return convertToVO(comment);
    }

    @Override
    public Page<CommentVO> getCommentsByPost(Long postId, Long current, Long size) {
        // 查询顶级评论（parent_comment_id 为空）
        Page<PostComments> page = new Page<>(current, size);
        LambdaQueryWrapper<PostComments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostComments::getPost_id, postId)
               .isNull(PostComments::getParent_comment_id)
               .eq(PostComments::getDeleted, 0)
               .eq(PostComments::getReviewed, 1)
               .orderByDesc(PostComments::getCreated_at);

        Page<PostComments> commentPage = this.page(page, wrapper);

        // 转换为VO并加载回复
        Page<CommentVO> voPage = new Page<>(current, size);
        voPage.setTotal(commentPage.getTotal());
        voPage.setRecords(commentPage.getRecords().stream()
            .map(this::convertToVOWithReplies)
            .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public CommentVO getCommentDetail(Long commentId) {
        PostComments comment = this.getById(commentId);
        if (comment == null || comment.getDeleted() == 1) {
            throw new BusinessException(StatusCode.COMMENT_NOT_FOUND);
        }
        return convertToVOWithReplies(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        PostComments comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(StatusCode.COMMENT_NOT_FOUND);
        }

        // 验证是否是评论作者
        if (!comment.getUser_id().equals(userId)) {
            throw new BusinessException(StatusCode.FORBIDDEN);
        }

        // 软删除
        comment.setDeleted(1);
        this.updateById(comment);

        // 更新帖子评论数
        Posts post = postsService.getById(comment.getPost_id());
        if (post != null) {
            post.setComments_count(Math.max(0, post.getComments_count() - 1));
            postsService.updateById(post);
        }

        log.info("评论删除成功: commentId={}, userId={}", commentId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId) {
        PostComments comment = this.getById(commentId);
        if (comment == null || comment.getDeleted() == 1) {
            throw new BusinessException(StatusCode.COMMENT_NOT_FOUND);
        }

        comment.setLikes_count(comment.getLikes_count() + 1);
        this.updateById(comment);

        log.info("评论点赞成功: commentId={}, newCount={}", commentId, comment.getLikes_count());
    }

    @Override
    public List<CommentVO> getReplies(Long commentId) {
        LambdaQueryWrapper<PostComments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostComments::getParent_comment_id, commentId)
               .eq(PostComments::getDeleted, 0)
               .eq(PostComments::getReviewed, 1)
               .orderByAsc(PostComments::getCreated_at);

        List<PostComments> replies = this.list(wrapper);
        return replies.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private CommentVO convertToVO(PostComments comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPost_id());
        vo.setUserId(comment.getUser_id());
        vo.setContent(comment.getContent());
        vo.setParentCommentId(comment.getParent_comment_id());
        vo.setLikesCount(comment.getLikes_count());
        vo.setCreatedAt(comment.getCreated_at());

        // 获取用户信息
        Users user = usersService.getById(comment.getUser_id());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatarUrl(user.getAvatar_url());
        }

        return vo;
    }

    /**
     * 转换为VO并包含回复
     */
    private CommentVO convertToVOWithReplies(PostComments comment) {
        CommentVO vo = convertToVO(comment);
        vo.setReplies(getReplies(comment.getId()));
        return vo;
    }
}
