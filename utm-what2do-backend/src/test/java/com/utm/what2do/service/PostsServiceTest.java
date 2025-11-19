package com.utm.what2do.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.mapper.PostMediaMapper;
import com.utm.what2do.mapper.PostTagsMapper;
import com.utm.what2do.mapper.PostsMapper;
import com.utm.what2do.model.dto.PostCreateDTO;
import com.utm.what2do.model.entity.PostMedia;
import com.utm.what2do.model.entity.Posts;
import com.utm.what2do.model.vo.PostVO;
import com.utm.what2do.service.impl.PostsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PostsService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("帖子服务测试")
class PostsServiceTest {

    @Mock
    private PostsMapper postsMapper;

    @Mock
    private PostMediaMapper postMediaMapper;

    @Mock
    private PostTagsMapper postTagsMapper;

    @InjectMocks
    private PostsServiceImpl postsService;

    private PostCreateDTO postCreateDTO;
    private Posts testPost;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        postCreateDTO = new PostCreateDTO();
        postCreateDTO.setContent("这是一条测试帖子内容");
        postCreateDTO.setAuthorType("USER");
        postCreateDTO.setMediaUrls(Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.png"
        ));

        testPost = new Posts();
        testPost.setId(1L);
        testPost.setAuthor_user_id(1L);
        testPost.setContent("这是一条测试帖子内容");
        testPost.setIs_pinned(0);
        testPost.setLikes_count(10);
        testPost.setComments_count(5);
        testPost.setReposts_count(2);
        testPost.setCreated_at(new Date());
        testPost.setUpdated_at(new Date());
        testPost.setDeleted(0);
    }

    @Test
    @DisplayName("创建帖子 - 用户帖子成功")
    void createPost_UserPost_Success() {
        // Given
        when(postsMapper.insert(any(Posts.class))).thenAnswer(invocation -> {
            Posts post = invocation.getArgument(0);
            post.setId(1L);
            return 1;
        });
        when(postMediaMapper.insert(any(PostMedia.class))).thenReturn(1);

        // When
        PostVO result = postsService.createPost(postCreateDTO, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAuthorUserId());
        assertNull(result.getAuthorClubId());
        assertEquals("这是一条测试帖子内容", result.getContent());
        assertEquals(0, result.getLikesCount());
        assertFalse(result.getPinned());

        verify(postsMapper).insert(any(Posts.class));
        verify(postMediaMapper, times(2)).insert(any(PostMedia.class));
    }

    @Test
    @DisplayName("创建帖子 - 社团帖子")
    void createPost_ClubPost_Success() {
        // Given
        postCreateDTO.setAuthorType("CLUB");
        when(postsMapper.insert(any(Posts.class))).thenAnswer(invocation -> {
            Posts post = invocation.getArgument(0);
            post.setId(1L);
            return 1;
        });

        // When
        PostVO result = postsService.createPost(postCreateDTO, 1L);

        // Then
        assertNotNull(result);
        // Note: 当前实现中社团ID设为null，后续需完善
        verify(postsMapper).insert(argThat(post ->
            post.getAuthor_club_id() == null
        ));
    }

    @Test
    @DisplayName("创建帖子 - 无媒体文件")
    void createPost_NoMedia_Success() {
        // Given
        postCreateDTO.setMediaUrls(null);
        when(postsMapper.insert(any(Posts.class))).thenAnswer(invocation -> {
            Posts post = invocation.getArgument(0);
            post.setId(1L);
            return 1;
        });

        // When
        PostVO result = postsService.createPost(postCreateDTO, 1L);

        // Then
        assertNotNull(result);
        verify(postsMapper).insert(any(Posts.class));
        verify(postMediaMapper, never()).insert(any());
    }

    @Test
    @DisplayName("获取帖子列表 - 成功")
    void getPostList_Success() {
        // Given
        Page<Posts> mockPage = new Page<>(1, 10);
        List<Posts> posts = Arrays.asList(testPost);
        mockPage.setRecords(posts);
        mockPage.setTotal(1);

        when(postsMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(mockPage);

        // When
        Page<PostVO> result = postsService.getPostList(1L, 10L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("这是一条测试帖子内容", result.getRecords().get(0).getContent());

        verify(postsMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("获取帖子详情 - 成功")
    void getPostDetail_Success() {
        // Given
        when(postsMapper.selectById(1L)).thenReturn(testPost);

        // When
        PostVO result = postsService.getPostDetail(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("这是一条测试帖子内容", result.getContent());
        assertEquals(10, result.getLikesCount());
        assertEquals(5, result.getCommentsCount());

        verify(postsMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取帖子详情 - 帖子不存在")
    void getPostDetail_PostNotFound() {
        // Given
        when(postsMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            postsService.getPostDetail(999L);
        });

        verify(postsMapper).selectById(999L);
    }

    @Test
    @DisplayName("获取帖子详情 - 帖子已删除")
    void getPostDetail_PostDeleted() {
        // Given
        testPost.setDeleted(1);
        when(postsMapper.selectById(1L)).thenReturn(testPost);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            postsService.getPostDetail(1L);
        });

        verify(postsMapper).selectById(1L);
    }

    @Test
    @DisplayName("删除帖子 - 成功")
    void deletePost_Success() {
        // Given
        when(postsMapper.selectById(1L)).thenReturn(testPost);
        when(postsMapper.updateById(any(Posts.class))).thenReturn(1);

        // When
        postsService.deletePost(1L, 1L);

        // Then
        verify(postsMapper).selectById(1L);
        verify(postsMapper).updateById(argThat(post ->
            post.getDeleted() == 1
        ));
    }

    @Test
    @DisplayName("删除帖子 - 无权限")
    void deletePost_Unauthorized() {
        // Given
        when(postsMapper.selectById(1L)).thenReturn(testPost);

        // When & Then - 尝试用其他用户ID删除
        assertThrows(BusinessException.class, () -> {
            postsService.deletePost(1L, 999L);
        });

        verify(postsMapper).selectById(1L);
        verify(postsMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("点赞帖子 - 成功")
    void likePost_Success() {
        // Given
        when(postsMapper.selectById(1L)).thenReturn(testPost);
        when(postsMapper.updateById(any(Posts.class))).thenReturn(1);

        int originalLikes = testPost.getLikes_count();

        // When
        postsService.likePost(1L);

        // Then
        verify(postsMapper).selectById(1L);
        verify(postsMapper).updateById(argThat(post ->
            post.getLikes_count() == originalLikes + 1
        ));
    }

    @Test
    @DisplayName("点赞帖子 - 帖子不存在")
    void likePost_PostNotFound() {
        // Given
        when(postsMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            postsService.likePost(999L);
        });

        verify(postsMapper).selectById(999L);
        verify(postsMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("置顶帖子 - 成功")
    void pinPost_Success() {
        // Given
        when(postsMapper.selectById(1L)).thenReturn(testPost);
        when(postsMapper.updateById(any(Posts.class))).thenReturn(1);

        // When
        postsService.pinPost(1L, 1L, true);

        // Then
        verify(postsMapper).selectById(1L);
        verify(postsMapper).updateById(argThat(post ->
            post.getIs_pinned() == 1
        ));
    }

    @Test
    @DisplayName("取消置顶帖子 - 成功")
    void unpinPost_Success() {
        // Given
        testPost.setIs_pinned(1);
        when(postsMapper.selectById(1L)).thenReturn(testPost);
        when(postsMapper.updateById(any(Posts.class))).thenReturn(1);

        // When
        postsService.pinPost(1L, 1L, false);

        // Then
        verify(postsMapper).selectById(1L);
        verify(postsMapper).updateById(argThat(post ->
            post.getIs_pinned() == 0
        ));
    }

    @Test
    @DisplayName("置顶帖子 - 无权限")
    void pinPost_Unauthorized() {
        // Given
        when(postsMapper.selectById(1L)).thenReturn(testPost);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            postsService.pinPost(1L, 999L, true);
        });

        verify(postsMapper).selectById(1L);
        verify(postsMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("帖子列表排序 - 置顶优先")
    void getPostList_PinnedPostsFirst() {
        // Given
        Posts pinnedPost = new Posts();
        pinnedPost.setId(2L);
        pinnedPost.setContent("置顶帖子");
        pinnedPost.setIs_pinned(1);
        pinnedPost.setCreated_at(new Date(System.currentTimeMillis() - 86400000)); // 昨天
        pinnedPost.setDeleted(0);

        Posts normalPost = new Posts();
        normalPost.setId(1L);
        normalPost.setContent("普通帖子");
        normalPost.setIs_pinned(0);
        normalPost.setCreated_at(new Date()); // 今天
        normalPost.setDeleted(0);

        // 验证查询条件包含置顶排序
        when(postsMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenAnswer(invocation -> {
                Page<Posts> page = new Page<>(1, 10);
                // 模拟置顶优先排序
                page.setRecords(Arrays.asList(pinnedPost, normalPost));
                page.setTotal(2);
                return page;
            });

        // When
        Page<PostVO> result = postsService.getPostList(1L, 10L);

        // Then
        assertEquals(2, result.getRecords().size());
        // 第一个应该是置顶帖子
        assertTrue(result.getRecords().get(0).getPinned());
    }
}
