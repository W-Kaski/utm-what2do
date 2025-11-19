package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.dto.CommentCreateDTO;
import com.utm.what2do.model.vo.CommentVO;
import com.utm.what2do.service.PostCommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理Controller
 */
@Tag(name = "评论管理", description = "帖子评论的发布、查看、删除、点赞等API")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final PostCommentsService postCommentsService;

    /**
     * 获取帖子的评论列表
     */
    @Operation(summary = "获取评论列表", description = "分页获取指定帖子的评论列表，包含回复")
    @GetMapping("/post/{postId}")
    public ResultVO<Page<CommentVO>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<CommentVO> page = postCommentsService.getCommentsByPost(postId, current, size);
        return ResultVO.success(page);
    }

    /**
     * 获取评论详情
     */
    @Operation(summary = "获取评论详情", description = "获取评论详情及其回复")
    @GetMapping("/{id}")
    public ResultVO<CommentVO> getCommentDetail(@PathVariable Long id) {
        CommentVO comment = postCommentsService.getCommentDetail(id);
        return ResultVO.success(comment);
    }

    /**
     * 获取评论的回复
     */
    @Operation(summary = "获取评论回复", description = "获取指定评论的所有回复")
    @GetMapping("/{id}/replies")
    public ResultVO<List<CommentVO>> getReplies(@PathVariable Long id) {
        List<CommentVO> replies = postCommentsService.getReplies(id);
        return ResultVO.success(replies);
    }

    /**
     * 发布评论
     */
    @Operation(summary = "发布评论", description = "对帖子发布评论或回复其他评论")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping
    public ResultVO<CommentVO> createComment(@Valid @RequestBody CommentCreateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        CommentVO comment = postCommentsService.createComment(dto, userId);
        return ResultVO.success("评论发布成功", comment);
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论", description = "删除自己发布的评论（软删除）")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/{id}")
    public ResultVO<Void> deleteComment(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        postCommentsService.deleteComment(id, userId);
        return ResultVO.success("评论删除成功");
    }

    /**
     * 点赞评论
     */
    @Operation(summary = "点赞评论", description = "增加评论的点赞计数")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/{id}/like")
    public ResultVO<Void> likeComment(@PathVariable Long id) {
        postCommentsService.likeComment(id);
        return ResultVO.success("点赞成功");
    }
}
