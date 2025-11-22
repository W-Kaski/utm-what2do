package com.utm.what2do.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.constant.CacheConstants;
import com.utm.what2do.mapper.PostCommentsMapper;
import com.utm.what2do.mapper.PostMediaMapper;
import com.utm.what2do.mapper.PostTagsMapper;
import com.utm.what2do.model.dto.PostCommentDTO;
import com.utm.what2do.model.dto.PostCreateDTO;
import com.utm.what2do.model.entity.PostComments;
import com.utm.what2do.model.entity.PostMedia;
import com.utm.what2do.model.entity.Posts;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.PostCommentVO;
import com.utm.what2do.model.vo.PostVO;
import com.utm.what2do.service.PostsService;
import com.utm.what2do.mapper.PostsMapper;
import com.utm.what2do.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final PostCommentsMapper postCommentsMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Lazy
    private final UsersService usersService;

    private static final int MAX_CONTENT_LENGTH = 500;
    private static final int MAX_COMMENT_LENGTH = 200;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostVO createPost(PostCreateDTO dto, Long userId) {
        // 1. 内容校验
        if (StrUtil.isBlank(dto.getContent())) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "帖子内容不能为空");
        }
        if (dto.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                "帖子内容不能超过" + MAX_CONTENT_LENGTH + "个字符");
        }

        // 2. 创建帖子主体
        Posts post = new Posts();
        post.setContent(dto.getContent());

        // 设置作者（根据authorType判断是用户还是社团）
        if ("CLUB".equals(dto.getAuthorType())) {
            if (dto.getClubId() == null) {
                throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "社团发帖必须提供 clubId");
            }
            post.setAuthor_club_id(dto.getClubId());
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

        // 3. 保存媒体文件
        if (dto.getMediaUrls() != null && !dto.getMediaUrls().isEmpty()) {
            for (int i = 0; i < dto.getMediaUrls().size(); i++) {
                PostMedia media = new PostMedia();
                media.setPost_id(post.getId());
                media.setUrl(dto.getMediaUrls().get(i));
                media.setOrder_index(i);
                media.setCreated_at(new Date());
                // 根据URL判断类型
                String url = dto.getMediaUrls().get(i).toLowerCase();
                if (url.matches(".*\\.(jpg|jpeg|png|gif|webp).*")) {
                    media.setType("IMAGE");
                } else if (url.matches(".*\\.(mp4|mov|avi|webm).*")) {
                    media.setType("VIDEO");
                } else {
                    media.setType("LINK");
                }
                postMediaMapper.insert(media);
            }
            log.info("保存帖子媒体: postId={}, mediaCount={}", post.getId(), dto.getMediaUrls().size());
        }

        // 4. 清除列表缓存
        clearListCache();

        return convertToVO(post);
    }

    @Override
    public Page<PostVO> getPostList(Long current, Long size, String sortBy, String tag) {
        // 尝试从缓存读取
        String cacheKey = buildListCacheKey(current, size, sortBy, tag);
        @SuppressWarnings("unchecked")
        Page<PostVO> cached = (Page<PostVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从缓存读取帖子列表: key={}", cacheKey);
            return cached;
        }

        Page<Posts> page = new Page<>(current, size);

        LambdaQueryWrapper<Posts> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Posts::getDeleted, 0);

        // 排序
        wrapper.orderByDesc(Posts::getIs_pinned); // 置顶优先
        if ("likes".equals(sortBy)) {
            wrapper.orderByDesc(Posts::getLikes_count);
        } else {
            wrapper.orderByDesc(Posts::getCreated_at);
        }

        Page<Posts> postsPage = this.page(page, wrapper);

        Page<PostVO> voPage = new Page<>(postsPage.getCurrent(), postsPage.getSize(), postsPage.getTotal());
        List<PostVO> voList = postsPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        voPage.setRecords(voList);

        // 缓存结果 (10分钟)
        redisTemplate.opsForValue().set(cacheKey, voPage, 10, TimeUnit.MINUTES);

        log.info("查询帖子列表: current={}, size={}, total={}", current, size, voPage.getTotal());

        return voPage;
    }

    @Override
    public PostVO getPostDetail(Long postId) {
        // 尝试从缓存读取
        String cacheKey = CacheConstants.POST_DETAIL_KEY + postId;
        PostVO cached = (PostVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从缓存读取帖子详情: postId={}", postId);
            return cached;
        }

        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND.getCode(), "帖子不存在");
        }

        PostVO vo = convertToVO(post);

        // 获取媒体
        LambdaQueryWrapper<PostMedia> mediaQuery = new LambdaQueryWrapper<>();
        mediaQuery.eq(PostMedia::getPost_id, postId)
                  .orderByAsc(PostMedia::getOrder_index);
        List<PostMedia> mediaList = postMediaMapper.selectList(mediaQuery);
        vo.setMediaUrls(mediaList.stream().map(PostMedia::getUrl).collect(Collectors.toList()));

        // 缓存结果 (1小时)
        redisTemplate.opsForValue().set(cacheKey, vo,
            CacheConstants.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("查询帖子详情: postId={}", postId);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND.getCode(), "帖子不存在");
        }

        // 权限检查：只有作者可以删除
        if (!userId.equals(post.getAuthor_user_id()) &&
            (post.getAuthor_club_id() == null || !userId.equals(post.getAuthor_club_id()))) {
            throw new BusinessException(StatusCode.FORBIDDEN.getCode(), "无权删除该帖子");
        }

        // 软删除帖子
        post.setDeleted(1);
        post.setUpdated_at(new Date());
        this.updateById(post);

        // 软删除关联的评论
        LambdaUpdateWrapper<PostComments> commentUpdate = new LambdaUpdateWrapper<>();
        commentUpdate.eq(PostComments::getPost_id, postId)
                     .set(PostComments::getDeleted, 1);
        postCommentsMapper.update(null, commentUpdate);

        // 清除缓存
        clearPostCache(postId);

        log.info("删除帖子: postId={}, userId={}", postId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long postId) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND.getCode(), "帖子不存在");
        }

        // 原子性更新点赞计数
        LambdaUpdateWrapper<Posts> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Posts::getId, postId)
                     .setSql("likes_count = likes_count + 1");
        this.update(updateWrapper);

        // 清除缓存
        clearPostCache(postId);

        log.info("点赞帖子: postId={}", postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pinPost(Long postId, Long userId, Boolean pinned) {
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND.getCode(), "帖子不存在");
        }

        // 权限检查
        if (!userId.equals(post.getAuthor_user_id()) &&
            (post.getAuthor_club_id() == null || !userId.equals(post.getAuthor_club_id()))) {
            throw new BusinessException(StatusCode.FORBIDDEN.getCode(), "无权置顶该帖子");
        }

        post.setIs_pinned(pinned ? 1 : 0);
        post.setUpdated_at(new Date());
        this.updateById(post);

        // 清除缓存
        clearPostCache(postId);
        clearListCache();

        log.info("{}帖子: postId={}, userId={}", pinned ? "置顶" : "取消置顶", postId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostCommentVO addComment(Long postId, PostCommentDTO dto, Long userId) {
        // 1. 校验帖子存在
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND.getCode(), "帖子不存在");
        }

        // 2. 内容校验
        if (StrUtil.isBlank(dto.getContent())) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "评论内容不能为空");
        }
        if (dto.getContent().length() > MAX_COMMENT_LENGTH) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                "评论内容不能超过" + MAX_COMMENT_LENGTH + "个字符");
        }

        // 3. 创建评论
        PostComments comment = new PostComments();
        comment.setPost_id(postId);
        comment.setUser_id(userId);
        comment.setContent(dto.getContent());
        comment.setParent_comment_id(dto.getParentCommentId());
        comment.setLikes_count(0);
        comment.setReviewed(1);
        comment.setCreated_at(new Date());
        comment.setDeleted(0);

        postCommentsMapper.insert(comment);

        // 4. 原子性更新帖子评论计数
        LambdaUpdateWrapper<Posts> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Posts::getId, postId)
                     .setSql("comments_count = comments_count + 1");
        this.update(updateWrapper);

        // 5. 清除缓存
        clearPostCache(postId);

        log.info("添加评论: postId={}, commentId={}, userId={}", postId, comment.getId(), userId);

        return convertToCommentVO(comment);
    }

    @Override
    public List<PostCommentVO> getComments(Long postId) {
        // 校验帖子存在
        Posts post = this.getById(postId);
        if (post == null || post.getDeleted() == 1) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND.getCode(), "帖子不存在");
        }

        LambdaQueryWrapper<PostComments> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostComments::getPost_id, postId)
               .eq(PostComments::getDeleted, 0)
               .orderByAsc(PostComments::getCreated_at);

        List<PostComments> comments = postCommentsMapper.selectList(wrapper);

        return comments.stream()
            .map(this::convertToCommentVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        PostComments comment = postCommentsMapper.selectById(commentId);
        if (comment == null || comment.getDeleted() == 1) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "评论不存在");
        }

        // 权限检查：只有评论作者可以删除
        if (!userId.equals(comment.getUser_id())) {
            throw new BusinessException(StatusCode.FORBIDDEN.getCode(), "无权删除该评论");
        }

        // 软删除评论
        comment.setDeleted(1);
        postCommentsMapper.updateById(comment);

        // 原子性更新帖子评论计数
        LambdaUpdateWrapper<Posts> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Posts::getId, comment.getPost_id())
                     .setSql("comments_count = GREATEST(comments_count - 1, 0)");
        this.update(updateWrapper);

        // 清除缓存
        clearPostCache(comment.getPost_id());

        log.info("删除评论: commentId={}, userId={}", commentId, userId);
    }

    @Override
    public void clearPostCache(Long postId) {
        String detailKey = CacheConstants.POST_DETAIL_KEY + postId;
        redisTemplate.delete(detailKey);
        clearListCache();
        log.debug("清除帖子缓存: postId={}", postId);
    }

    /**
     * 清除列表缓存
     */
    private void clearListCache() {
        redisTemplate.delete(redisTemplate.keys(CacheConstants.POST_LIST_KEY + "*"));
    }

    /**
     * 构建列表缓存Key
     */
    private String buildListCacheKey(Long current, Long size, String sortBy, String tag) {
        return CacheConstants.POST_LIST_KEY +
               (sortBy != null ? sortBy : "time") + ":" +
               (tag != null ? tag : "all") + ":" +
               current + ":" + size;
    }

    /**
     * 转换为PostVO
     */
    private PostVO convertToVO(Posts post) {
        PostVO vo = PostVO.builder()
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

        // 获取作者信息
        if (post.getAuthor_user_id() != null) {
            try {
                Users user = usersService.getById(post.getAuthor_user_id());
                if (user != null) {
                    vo.setAuthorName(user.getDisplay_name() != null ? user.getDisplay_name() : user.getUsername());
                    vo.setAuthorAvatar(user.getAvatar_url());
                }
            } catch (Exception e) {
                log.warn("获取作者信息失败: userId={}", post.getAuthor_user_id(), e);
            }
        }

        return vo;
    }

    /**
     * 转换为PostCommentVO
     */
    private PostCommentVO convertToCommentVO(PostComments comment) {
        PostCommentVO vo = PostCommentVO.builder()
            .id(comment.getId())
            .postId(comment.getPost_id())
            .userId(comment.getUser_id())
            .content(comment.getContent())
            .parentCommentId(comment.getParent_comment_id())
            .likesCount(comment.getLikes_count())
            .createdAt(comment.getCreated_at() != null ?
                LocalDateTime.ofInstant(comment.getCreated_at().toInstant(), ZoneId.systemDefault()) : null)
            .build();

        // 获取评论者信息
        if (comment.getUser_id() != null) {
            try {
                Users user = usersService.getById(comment.getUser_id());
                if (user != null) {
                    vo.setAuthorName(user.getDisplay_name() != null ? user.getDisplay_name() : user.getUsername());
                    vo.setAuthorAvatar(user.getAvatar_url());
                }
            } catch (Exception e) {
                log.warn("获取评论者信息失败: userId={}", comment.getUser_id(), e);
            }
        }

        return vo;
    }
}
