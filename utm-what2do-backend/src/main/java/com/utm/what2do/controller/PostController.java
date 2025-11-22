package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.dto.PostCommentDTO;
import com.utm.what2do.model.dto.PostCreateDTO;
import com.utm.what2do.model.vo.PostCommentVO;
import com.utm.what2do.model.vo.PostVO;
import com.utm.what2do.service.PostsService;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子管理Controller
 */
@Tag(name = "帖子管理", description = "帖子列表、详情、发布、点赞等API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostsService postsService;

    /**
     * 获取帖子列表（分页）
     */
    @Operation(summary = "获取帖子列表", description = "分页获取帖子列表，支持排序和标签筛选")
    @GetMapping
    public ResultVO<Page<PostVO>> getPostList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(required = false) String tag) {
        Page<PostVO> page = postsService.getPostList(current, size, sortBy, tag);
        return ResultVO.success(page);
    }

    /**
     * 获取帖子详情
     */
    @Operation(summary = "获取帖子详情", description = "根据帖子ID获取详细信息")
    @GetMapping("/{id}")
    public ResultVO<PostVO> getPostDetail(@PathVariable Long id) {
        PostVO detail = postsService.getPostDetail(id);
        return ResultVO.success(detail);
    }

    /**
     * 发布新帖子
     */
    @Operation(summary = "发布新帖子", description = "用户或社团发布新帖子")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping
    public ResultVO<PostVO> createPost(@Valid @RequestBody PostCreateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        PostVO post = postsService.createPost(dto, userId);
        return ResultVO.success("帖子发布成功", post);
    }

    /**
     * 删除帖子
     */
    @Operation(summary = "删除帖子", description = "删除自己发布的帖子（软删除）")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/{id}")
    public ResultVO<Void> deletePost(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        postsService.deletePost(id, userId);
        return ResultVO.success("帖子删除成功");
    }

    /**
     * 点赞帖子
     */
    @Operation(summary = "点赞帖子", description = "增加帖子的点赞计数")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/{id}/like")
    public ResultVO<Void> likePost(@PathVariable Long id) {
        postsService.likePost(id);
        return ResultVO.success("点赞成功");
    }

    /**
     * 置顶/取消置顶帖子
     */
    @Operation(summary = "置顶帖子", description = "置顶或取消置顶自己的帖子")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PutMapping("/{id}/pin")
    public ResultVO<Void> pinPost(
            @PathVariable Long id,
            @RequestParam Boolean pinned) {
        Long userId = StpUtil.getLoginIdAsLong();
        postsService.pinPost(id, userId, pinned);
        return ResultVO.success(pinned ? "置顶成功" : "取消置顶成功");
    }

    /**
     * 发布评论
     */
    @Operation(summary = "发布评论", description = "在帖子下发布评论")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/{id}/comments")
    public ResultVO<PostCommentVO> addComment(
            @PathVariable Long id,
            @Valid @RequestBody PostCommentDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        PostCommentVO comment = postsService.addComment(id, dto, userId);
        return ResultVO.success("评论发布成功", comment);
    }

    /**
     * 获取评论列表
     */
    @Operation(summary = "获取评论列表", description = "获取帖子下的所有评论")
    @GetMapping("/{id}/comments")
    public ResultVO<List<PostCommentVO>> getComments(@PathVariable Long id) {
        List<PostCommentVO> comments = postsService.getComments(id);
        return ResultVO.success(comments);
    }

    /**
     * 删除评论
     */
    @Operation(summary = "删除评论", description = "删除自己发布的评论")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/comments/{commentId}")
    public ResultVO<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        postsService.deleteComment(commentId, userId);
        return ResultVO.success("评论删除成功");
    }
}
