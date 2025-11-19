package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.ClubsMapper;
import com.utm.what2do.mapper.EventsMapper;
import com.utm.what2do.model.entity.Buildings;
import com.utm.what2do.model.entity.Clubs;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.vo.ClubDetailVO;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.service.BuildingsService;
import com.utm.what2do.service.ClubsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author PC
* @description 针对表【clubs】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class ClubsServiceImpl extends ServiceImpl<ClubsMapper, Clubs>
    implements ClubsService{

    private final EventsMapper eventsMapper;
    private final BuildingsService buildingsService;

    @Override
    public List<Clubs> getAllClubs() {
        LambdaQueryWrapper<Clubs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clubs::getDeleted, 0);
        wrapper.orderByDesc(Clubs::getMembers_count);

        List<Clubs> clubs = this.list(wrapper);
        log.info("查询所有社团: count={}", clubs.size());

        return clubs;
    }

    @Override
    public ClubDetailVO getClubDetail(Long clubId) {
        Clubs club = this.getById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        return convertToDetailVO(club);
    }

    @Override
    public ClubDetailVO getClubDetailBySlug(String slug) {
        LambdaQueryWrapper<Clubs> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clubs::getSlug, slug);
        wrapper.eq(Clubs::getDeleted, 0);

        Clubs club = this.getOne(wrapper);
        if (club == null) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        return convertToDetailVO(club);
    }

    /**
     * 转换为ClubDetailVO
     */
    private ClubDetailVO convertToDetailVO(Clubs club) {
        ClubDetailVO vo = new ClubDetailVO();
        vo.setId(club.getId());
        vo.setName(club.getName());
        vo.setSlug(club.getSlug());
        vo.setBio(club.getDescription());
        vo.setLogoUrl(club.getLogo_url());
        vo.setCoverImageUrl(club.getCover_url());
        vo.setFollowersCount(club.getMembers_count());

        // 转换创建时间
        if (club.getCreated_at() != null) {
            vo.setCreatedAt(LocalDateTime.ofInstant(
                club.getCreated_at().toInstant(), ZoneId.systemDefault()));
        }

        // 查询即将举办的活动
        List<EventCardVO> upcomingEvents = getUpcomingEvents(club.getId());
        vo.setUpcomingEvents(upcomingEvents);

        log.info("社团详情查询成功: clubId={}, name={}, upcomingEventsCount={}",
            club.getId(), club.getName(), upcomingEvents.size());

        return vo;
    }

    /**
     * 获取社团即将举办的活动
     */
    private List<EventCardVO> getUpcomingEvents(Long clubId) {
        // 查询该社团未来的活动
        LambdaQueryWrapper<Events> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Events::getClub_id, clubId);
        wrapper.eq(Events::getDeleted, 0);
        wrapper.ge(Events::getStart_time, new Date()); // 开始时间大于等于现在
        wrapper.orderByAsc(Events::getStart_time);
        wrapper.last("LIMIT 10"); // 最多返回10个即将举办的活动

        List<Events> events = eventsMapper.selectList(wrapper);

        return events.stream()
            .map(this::convertToEventCardVO)
            .collect(Collectors.toList());
    }

    /**
     * 转换Events为EventCardVO
     */
    private EventCardVO convertToEventCardVO(Events event) {
        EventCardVO vo = new EventCardVO();
        vo.setId(event.getId());
        vo.setTitle(event.getTitle());
        vo.setSlug(event.getSlug());

        // 转换时间
        if (event.getStart_time() != null) {
            vo.setStartTime(LocalDateTime.ofInstant(
                event.getStart_time().toInstant(), ZoneId.systemDefault()));
        }
        if (event.getEnd_time() != null) {
            vo.setEndTime(LocalDateTime.ofInstant(
                event.getEnd_time().toInstant(), ZoneId.systemDefault()));
        }

        // 获取建筑信息
        try {
            Buildings building = buildingsService.getBuildingByStringId(event.getBuilding_id());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        } catch (Exception e) {
            log.warn("获取建筑信息失败: buildingId={}", event.getBuilding_id(), e);
        }
        vo.setRoom(event.getRoom());

        vo.setThumbnailUrl(event.getCover_url());
        vo.setInterestedCount(event.getLikes_count());
        vo.setStatus("UPCOMING"); // 查询的都是未来的活动

        return vo;
    }
}
