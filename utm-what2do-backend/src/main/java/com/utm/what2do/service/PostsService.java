package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.dto.PostCreateDTO;
import com.utm.what2do.model.entity.Posts;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.PostVO;

/**
* @author PC
* @description 针对表【posts】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface PostsService extends IService<Posts> {

    /**
     * 创建新帖子
     * @param dto 帖子创建DTO
     * @param userId 用户ID
     * @return 创建的帖子VO
     */
    PostVO createPost(PostCreateDTO dto, Long userId);

    /**
     * 获取帖子列表（分页）
     * @param current 当前页
     * @param size 每页大小
     * @return 帖子列表
     */
    Page<PostVO> getPostList(Long current, Long size);

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @return 帖子详情
     */
    PostVO getPostDetail(Long postId);

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void deletePost(Long postId, Long userId);

    /**
     * 点赞帖子
     * @param postId 帖子ID
     */
    void likePost(Long postId);

    /**
     * 置顶/取消置顶帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param pinned 是否置顶
     */
    void pinPost(Long postId, Long userId, Boolean pinned);
}
