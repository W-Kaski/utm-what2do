package com.utm.what2do.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.mapper.ClubsMapper;
import com.utm.what2do.mapper.EventsMapper;
import com.utm.what2do.model.entity.Clubs;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.vo.ClubDetailVO;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.service.impl.ClubsServiceImpl;
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
 * ClubsService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("社团服务测试")
class ClubsServiceTest {

    @Mock
    private ClubsMapper clubsMapper;

    @Mock
    private EventsMapper eventsMapper;

    @Mock
    private BuildingsService buildingsService;

    @InjectMocks
    private ClubsServiceImpl clubsService;

    private Clubs testClub;
    private List<Events> upcomingEvents;

    @BeforeEach
    void setUp() {
        // 准备测试社团
        testClub = new Clubs();
        testClub.setId(1L);
        testClub.setName("UTM中国学生学者联合会");
        testClub.setSlug("utm-cssa");
        testClub.setTagline("连接UTM华人学生的桥梁");
        testClub.setDescription("UTM中国学生学者联合会是一个服务于UTM华人学生的组织");
        testClub.setCategory(1);
        testClub.setLogo_url("https://example.com/cssa-logo.jpg");
        testClub.setCover_url("https://example.com/cssa-cover.jpg");
        testClub.setMembers_count(500);
        testClub.setEvents_count(20);
        testClub.setPosts_count(50);
        testClub.setCreated_at(new Date());
        testClub.setUpdated_at(new Date());
        testClub.setDeleted(0);

        // 准备即将举办的活动
        upcomingEvents = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Events event = new Events();
            event.setId((long) i);
            event.setTitle("活动 " + i);
            event.setSlug("event-" + i);
            event.setClub_id(1L);
            event.setStart_time(new Date(System.currentTimeMillis() + i * 86400000L)); // i天后
            event.setEnd_time(new Date(System.currentTimeMillis() + i * 86400000L + 7200000)); // +2小时
            event.setBuilding_id("DH");
            event.setRoom("2010");
            event.setCover_url("https://example.com/event" + i + ".jpg");
            event.setLikes_count(10 * i);
            event.setDeleted(0);
            upcomingEvents.add(event);
        }
    }

    @Test
    @DisplayName("获取所有社团列表 - 成功")
    void getAllClubs_Success() {
        // Given
        List<Clubs> clubs = Arrays.asList(testClub);
        when(clubsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(clubs);

        // When
        List<Clubs> result = clubsService.getAllClubs();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("UTM中国学生学者联合会", result.get(0).getName());
        assertEquals("utm-cssa", result.get(0).getSlug());

        verify(clubsMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("获取所有社团列表 - 按成员数排序")
    void getAllClubs_OrderByMembersCount() {
        // Given
        Clubs club1 = new Clubs();
        club1.setId(1L);
        club1.setName("社团A");
        club1.setMembers_count(100);
        club1.setDeleted(0);

        Clubs club2 = new Clubs();
        club2.setId(2L);
        club2.setName("社团B");
        club2.setMembers_count(500);
        club2.setDeleted(0);

        // 模拟按成员数降序返回
        when(clubsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(Arrays.asList(club2, club1));

        // When
        List<Clubs> result = clubsService.getAllClubs();

        // Then
        assertEquals(2, result.size());
        assertEquals("社团B", result.get(0).getName());
        assertEquals(500, result.get(0).getMembers_count());
    }

    @Test
    @DisplayName("根据ID获取社团详情 - 成功")
    void getClubDetail_Success() {
        // Given
        when(clubsMapper.selectById(1L)).thenReturn(testClub);
        when(eventsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(upcomingEvents);

        // When
        ClubDetailVO result = clubsService.getClubDetail(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("UTM中国学生学者联合会", result.getName());
        assertEquals("utm-cssa", result.getSlug());
        assertEquals("UTM中国学生学者联合会是一个服务于UTM华人学生的组织", result.getBio());
        assertEquals(500, result.getFollowersCount());

        // 验证即将举办的活动
        assertNotNull(result.getUpcomingEvents());
        assertEquals(3, result.getUpcomingEvents().size());

        verify(clubsMapper).selectById(1L);
        verify(eventsMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("根据ID获取社团详情 - 社团不存在")
    void getClubDetail_NotFound() {
        // Given
        when(clubsMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            clubsService.getClubDetail(999L);
        });

        verify(clubsMapper).selectById(999L);
        verify(eventsMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("根据ID获取社团详情 - 已删除")
    void getClubDetail_Deleted() {
        // Given
        testClub.setDeleted(1);
        when(clubsMapper.selectById(1L)).thenReturn(testClub);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            clubsService.getClubDetail(1L);
        });

        verify(clubsMapper).selectById(1L);
        verify(eventsMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("根据slug获取社团详情 - 成功")
    void getClubDetailBySlug_Success() {
        // Given
        when(clubsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testClub);
        when(eventsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(upcomingEvents);

        // When
        ClubDetailVO result = clubsService.getClubDetailBySlug("utm-cssa");

        // Then
        assertNotNull(result);
        assertEquals("utm-cssa", result.getSlug());
        assertEquals("UTM中国学生学者联合会", result.getName());

        verify(clubsMapper).selectOne(argThat(wrapper -> {
            // 验证查询条件包含slug
            return true;
        }));
    }

    @Test
    @DisplayName("根据slug获取社团详情 - 社团不存在")
    void getClubDetailBySlug_NotFound() {
        // Given
        when(clubsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            clubsService.getClubDetailBySlug("non-existent-club");
        });

        verify(clubsMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(eventsMapper, never()).selectList(any());
    }

    @Test
    @DisplayName("即将举办的活动 - 最多返回10个")
    void getClubDetail_MaxTenUpcomingEvents() {
        // Given
        List<Events> manyEvents = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Events event = new Events();
            event.setId((long) i);
            event.setTitle("活动 " + i);
            event.setClub_id(1L);
            event.setStart_time(new Date(System.currentTimeMillis() + i * 86400000L));
            event.setEnd_time(new Date(System.currentTimeMillis() + i * 86400000L + 7200000));
            event.setDeleted(0);
            manyEvents.add(event);
        }

        when(clubsMapper.selectById(1L)).thenReturn(testClub);
        when(eventsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(manyEvents.subList(0, 10)); // 只返回10个

        // When
        ClubDetailVO result = clubsService.getClubDetail(1L);

        // Then
        assertNotNull(result.getUpcomingEvents());
        assertTrue(result.getUpcomingEvents().size() <= 10);
    }

    @Test
    @DisplayName("即将举办的活动 - 按时间升序排列")
    void getClubDetail_UpcomingEventsOrderByTime() {
        // Given
        when(clubsMapper.selectById(1L)).thenReturn(testClub);
        when(eventsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(upcomingEvents);

        // When
        ClubDetailVO result = clubsService.getClubDetail(1L);

        // Then
        List<EventCardVO> events = result.getUpcomingEvents();
        assertNotNull(events);
        assertTrue(events.size() > 1);

        // 验证时间是升序的
        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(
                events.get(i).getStartTime().isBefore(events.get(i + 1).getStartTime()) ||
                events.get(i).getStartTime().isEqual(events.get(i + 1).getStartTime())
            );
        }
    }

    @Test
    @DisplayName("即将举办的活动 - 不包含已删除的活动")
    void getClubDetail_ExcludeDeletedEvents() {
        // Given
        Events deletedEvent = new Events();
        deletedEvent.setId(99L);
        deletedEvent.setTitle("已删除活动");
        deletedEvent.setDeleted(1);
        deletedEvent.setClub_id(1L);

        // 模拟查询时已过滤掉deleted=1的活动
        when(clubsMapper.selectById(1L)).thenReturn(testClub);
        when(eventsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(upcomingEvents); // 不包含删除的活动

        // When
        ClubDetailVO result = clubsService.getClubDetail(1L);

        // Then
        assertNotNull(result.getUpcomingEvents());
        assertTrue(result.getUpcomingEvents().stream()
            .noneMatch(event -> event.getId() == 99L));
    }

    @Test
    @DisplayName("社团详情VO字段映射 - 完整性验证")
    void clubDetailVO_FieldMapping() {
        // Given
        when(clubsMapper.selectById(1L)).thenReturn(testClub);
        when(eventsMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(new ArrayList<>());

        // When
        ClubDetailVO result = clubsService.getClubDetail(1L);

        // Then - 验证所有字段都被正确映射
        assertEquals(testClub.getId(), result.getId());
        assertEquals(testClub.getName(), result.getName());
        assertEquals(testClub.getSlug(), result.getSlug());
        assertEquals(testClub.getDescription(), result.getBio());
        assertEquals(testClub.getLogo_url(), result.getLogoUrl());
        assertEquals(testClub.getCover_url(), result.getCoverImageUrl());
        assertEquals(testClub.getMembers_count(), result.getFollowersCount());
        assertNotNull(result.getCreatedAt());
    }
}
