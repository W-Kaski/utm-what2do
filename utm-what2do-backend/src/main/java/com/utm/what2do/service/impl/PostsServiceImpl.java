package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.PostMediaMapper;
import com.utm.what2do.mapper.PostTagsMapper;
import com.utm.what2do.model.dto.PostCreateDTO;
import com.utm.what2do.model.entity.PostMedia;
import com.utm.what2do.model.entity.Posts;
import com.utm.what2do.service.PostsService;
import com.utm.what2do.mapper.PostsMapper;
import com.utm.what2do.model.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
* @author PC
* @description 针对表【posts】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts>
    implements PostsService{

    private final PostMediaMapper postMediaMapper;
    private final PostTagsMapper postTagsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostVO createPost(PostCreateDTO dto, Long userId) {
        // 创建帖子主体
        Posts post = new Posts();
        post.setContent(dto.getContent());

        // 设置作者（根据authorType判断是用户还是社团）
        if ("CLUB".equals(dto.getAuthorType())) {
            // 这里应该从session或数据库获取用户管理的社团ID
            // 暂时设置为null，后续完善
            post.setAuthor_club_id(null);
        } else {
            post.setAuthor_user_id(userId);
        }

        post.setIs_pinned(0);
        post.setLikes_count(0);
        post.setComments_count(0);
        post.setReposts_count(0);
        post.setCreated_at(new Date());
        post.setUpdated_at(new Date());
        post.setDeleted(0);

        this.save(post);
        log.info("创建帖子成功: postId={}, userId={}", post.getId(), userId);

        // 保存媒体文件
        if (dto.getMediaUrls() != null && !dto.getMediaUrls().isEmpty()) {
            for (int i = 0; i < dto.getMediaUrls().size(); i++) {
                PostMedia media = new PostMedia();
                media.setPost_id(post.getId());
                media.setUrl(dto.getMediaUrls().get(i));
                media.setOrder_index(i);
                media.setCreated_at(new Date());
                // 根据URL判断类型（简化处理）
                if (dto.getMediaUrls().get(i).matches(".*\\.(jpg|jpeg|png|gif|webp).*")) {
                    media.setType("IMAGE");
                } else if (dto.getMediaUrls().get(i).matches(".*\\.(mp4|mov|avi).*")) {
                    media.setType("VIDEO");
                } else {
                    media.setType("LINK");
                }
                postMediaMapper.insert(media);
            }
            log.info("保存帖子媒体: postId={}, mediaCount={}", post.getId(), dto.getMediaUrls().size());
        }

        // TODO: 保存标签关联（需要先查询或创建tag）

        return convertToVO(post);
    }

    @Override
    public Page<PostVO> getPostList(Long current, Long size) {
        Page<Posts> page = new Page<>(current, size);

        LambdaQueryWrapper<Posts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Posts::getDeleted, 0);
        wrapper.orderByDesc(Posts::getIs_pinned); // 置顶优先
        wrapper.orderByDesc(Posts::getCreated_at); // 然后按时间排序

        Page<Posts> postsPage = this.page(page, wrapper);

        Page<PostVO> voPage = new Page<>(postsPage.getCurrent(), postsPage.getSize(), postsPage.getTotal());
        List<PostVO> voList = postsPage.getRecords().stream()
            .map(this::convertToVO)
            .toList();
        voPage.setRecords(voList);

        log.info("查询帖子列表: current={}, size={}, total={}", current, size, voPage.getTotal());

        return voPage;
    }

    @Override
    public PostVO getPostDetail(Long postId) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.PARAMS_ERROR.getCode(), "帖子不存在");
        }

        log.info("查询帖子详情: postId={}", postId);
        return convertToVO(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.PARAMS_ERROR.getCode(), "帖子不存在");
        }

        // 权限检查：只有作者可以删除
        if (!userId.equals(post.getAuthor_user_id()) &&
            (post.getAuthor_club_id() == null || !userId.equals(post.getAuthor_club_id()))) {
            throw new BusinessException(StatusCode.FORBIDDEN.getCode(), "无权删除该帖子");
        }

        // 软删除
        post.setDeleted(1);
        post.setUpdated_at(new Date());
        this.updateById(post);

        log.info("删除帖子: postId={}, userId={}", postId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long postId) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.PARAMS_ERROR.getCode(), "帖子不存在");
        }

        post.setLikes_count(post.getLikes_count() + 1);
        post.setUpdated_at(new Date());
        this.updateById(post);

        log.info("点赞帖子: postId={}, newCount={}", postId, post.getLikes_count());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pinPost(Long postId, Long userId, Boolean pinned) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.PARAMS_ERROR.getCode(), "帖子不存在");
        }

        // 权限检查：只有作者或管理员可以置顶
        // TODO: 添加管理员权限检查
        if (!userId.equals(post.getAuthor_user_id()) &&
            (post.getAuthor_club_id() == null || !userId.equals(post.getAuthor_club_id()))) {
            throw new BusinessException(StatusCode.FORBIDDEN.getCode(), "无权置顶该帖子");
        }

        post.setIs_pinned(pinned ? 1 : 0);
        post.setUpdated_at(new Date());
        this.updateById(post);

        log.info("{}帖子: postId={}, userId={}", pinned ? "置顶" : "取消置顶", postId, userId);
    }

    /**
     * 转换为PostVO
     */
    private PostVO convertToVO(Posts post) {
        return PostVO.builder()
            .id(post.getId())
            .authorUserId(post.getAuthor_user_id())
            .authorClubId(post.getAuthor_club_id())
            .content(post.getContent())
            .pinned(post.getIs_pinned() == 1)
            .likesCount(post.getLikes_count())
            .commentsCount(post.getComments_count())
            .repostsCount(post.getReposts_count())
            .createdAt(post.getCreated_at() != null ?
                LocalDateTime.ofInstant(post.getCreated_at().toInstant(), ZoneId.systemDefault()) : null)
            .updatedAt(post.getUpdated_at() != null ?
                LocalDateTime.ofInstant(post.getUpdated_at().toInstant(), ZoneId.systemDefault()) : null)
            .build();
    }
}




