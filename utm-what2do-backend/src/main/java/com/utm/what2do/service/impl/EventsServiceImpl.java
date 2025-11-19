package com.utm.what2do.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.common.util.DateUtils;
import com.utm.what2do.constant.CacheConstants;
import com.utm.what2do.constant.EventConstants;
import com.utm.what2do.mapper.EventsMapper;
import com.utm.what2do.model.dto.EventCreateDTO;
import com.utm.what2do.model.dto.EventFilterDTO;
import com.utm.what2do.model.entity.Buildings;
import com.utm.what2do.model.entity.Clubs;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.model.vo.EventDetailVO;
import com.utm.what2do.service.BuildingsService;
import com.utm.what2do.service.ClubsService;
import com.utm.what2do.service.EventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
* @description 针对表【events】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class EventsServiceImpl extends ServiceImpl<EventsMapper, Events>
    implements EventsService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final BuildingsService buildingsService;
    private final ClubsService clubsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDetailVO createEvent(EventCreateDTO dto, Long userId) {
        // 1. 验证建筑是否存在
        Buildings building = buildingsService.getById(Long.parseLong(dto.getBuildingId()));
        if (building == null) {
            throw new BusinessException(StatusCode.BUILDING_NOT_FOUND);
        }

        // 2. 验证社团是否存在
        Clubs club = clubsService.getById(dto.getOrganizerId());
        if (club == null) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 3. 创建活动实体
        Events event = new Events();
        event.setTitle(dto.getTitle());
        event.setSlug(dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getTitle()));
        event.setDescription_long(dto.getDescription());

        // 转换LocalDateTime为Date
        event.setStart_date(Date.from(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
        event.setStart_time(Date.from(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
        event.setEnd_time(Date.from(dto.getEndTime().atZone(ZoneId.systemDefault()).toInstant()));

        event.setBuilding_id(dto.getBuildingId());
        event.setRoom(dto.getRoom());
        event.setClub_id(dto.getOrganizerId());
        event.setCover_url(dto.getCoverImageUrl());
        event.setIs_official(1);
        event.setRegistration_notes(dto.getRegistrationUrl());
        event.setViews_count(0);
        event.setLikes_count(0);
        event.setCreated_at(new Date());
        event.setUpdated_at(new Date());
        event.setDeleted(0);

        // 4. 保存活动
        boolean saved = this.save(event);
        if (!saved) {
            throw new BusinessException(500, "活动创建失败");
        }

        log.info("活动创建成功: eventId={}, title={}, clubId={}",
            event.getId(), event.getTitle(), dto.getOrganizerId());

        // 5. 清除相关缓存
        clearEventCache(event.getId());

        // 6. 返回活动详情
        return getEventDetail(event.getId());
    }

    @Override
    public Page<EventCardVO> getEventList(EventFilterDTO filterDTO) {
        // 1. 尝试从Redis缓存读取
        String cacheKey = buildListCacheKey(filterDTO);
        @SuppressWarnings("unchecked")
        Page<EventCardVO> cachedPage = (Page<EventCardVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedPage != null) {
            log.debug("从Redis缓存读取活动列表: key={}", cacheKey);
            return cachedPage;
        }

        // 2. 构建查询条件
        LambdaQueryWrapper<Events> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Events::getDeleted, 0);

        // 时间筛选
        if (StrUtil.isNotBlank(filterDTO.getTimeFilter())) {
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            switch (filterDTO.getTimeFilter()) {
                case EventConstants.TIME_FILTER_TODAY:
                    startTime = DateUtils.getTodayStart();
                    endTime = DateUtils.getTodayEnd();
                    break;
                case EventConstants.TIME_FILTER_WEEK:
                    startTime = DateUtils.getWeekStart();
                    endTime = DateUtils.getWeekEnd();
                    break;
                case EventConstants.TIME_FILTER_MONTH:
                    startTime = DateUtils.getMonthStart();
                    endTime = DateUtils.getMonthEnd();
                    break;
            }

            if (startTime != null && endTime != null) {
                Date start = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
                Date end = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
                wrapper.between(Events::getStart_time, start, end);
            }
        }

        // 建筑筛选
        if (filterDTO.getBuildingIds() != null && !filterDTO.getBuildingIds().isEmpty()) {
            List<String> buildingIdStrs = filterDTO.getBuildingIds().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
            wrapper.in(Events::getBuilding_id, buildingIdStrs);
        }

        // 关键词搜索
        if (StrUtil.isNotBlank(filterDTO.getKeyword())) {
            wrapper.and(w -> w.like(Events::getTitle, filterDTO.getKeyword())
                .or().like(Events::getDescription_long, filterDTO.getKeyword()));
        }

        // 排序
        String sortBy = filterDTO.getSortBy();
        if (StrUtil.isBlank(sortBy)) {
            sortBy = EventConstants.SORT_BY_TIME_ASC;
        }

        switch (sortBy) {
            case EventConstants.SORT_BY_TIME_ASC:
                wrapper.orderByAsc(Events::getStart_time);
                break;
            case EventConstants.SORT_BY_TIME_DESC:
                wrapper.orderByDesc(Events::getStart_time);
                break;
            case EventConstants.SORT_BY_POPULARITY:
                wrapper.orderByDesc(Events::getLikes_count);
                break;
            case EventConstants.SORT_BY_CREATED:
                wrapper.orderByDesc(Events::getCreated_at);
                break;
        }

        // 3. 分页查询
        Page<Events> eventPage = this.page(
            new Page<>(filterDTO.getPage(), filterDTO.getPageSize()),
            wrapper
        );

        // 4. 转换为VO
        Page<EventCardVO> resultPage = new Page<>();
        resultPage.setCurrent(eventPage.getCurrent());
        resultPage.setSize(eventPage.getSize());
        resultPage.setTotal(eventPage.getTotal());
        resultPage.setPages(eventPage.getPages());

        List<EventCardVO> cardList = eventPage.getRecords().stream()
            .map(this::convertToCardVO)
            .collect(Collectors.toList());
        resultPage.setRecords(cardList);

        // 5. 缓存到Redis（1小时）
        redisTemplate.opsForValue().set(cacheKey, resultPage,
            CacheConstants.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("活动列表查询完成: total={}, page={}, size={}",
            resultPage.getTotal(), filterDTO.getPage(), filterDTO.getPageSize());

        return resultPage;
    }

    @Override
    public EventDetailVO getEventDetail(Long eventId) {
        // 1. 尝试从Redis缓存读取
        String cacheKey = CacheConstants.EVENT_DETAIL_KEY + eventId;
        EventDetailVO cached = (EventDetailVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从Redis缓存读取活动详情: eventId={}", eventId);
            // 异步增加浏览量
            incrementViews(eventId);
            return cached;
        }

        // 2. 从数据库查询
        Events event = this.getById(eventId);
        if (event == null || event.getDeleted() == 1) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 3. 转换为VO
        EventDetailVO detailVO = convertToDetailVO(event);

        // 4. 缓存到Redis（1小时）
        redisTemplate.opsForValue().set(cacheKey, detailVO,
            CacheConstants.CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        // 5. 异步增加浏览量
        incrementViews(eventId);

        log.info("活动详情查询成功: eventId={}, title={}", eventId, event.getTitle());

        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventDetailVO updateEvent(Long eventId, EventCreateDTO dto, Long userId) {
        // 1. 查询活动
        Events event = this.getById(eventId);
        if (event == null || event.getDeleted() == 1) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 2. 更新字段
        event.setTitle(dto.getTitle());
        event.setDescription_long(dto.getDescription());
        event.setStart_time(Date.from(dto.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
        event.setEnd_time(Date.from(dto.getEndTime().atZone(ZoneId.systemDefault()).toInstant()));
        event.setBuilding_id(dto.getBuildingId());
        event.setRoom(dto.getRoom());
        event.setCover_url(dto.getCoverImageUrl());
        event.setUpdated_at(new Date());

        // 3. 保存更新
        boolean updated = this.updateById(event);
        if (!updated) {
            throw new BusinessException(500, "活动更新失败");
        }

        // 4. 清除缓存
        clearEventCache(eventId);

        log.info("活动更新成功: eventId={}", eventId);

        return getEventDetail(eventId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementInterested(Long eventId) {
        Events event = this.getById(eventId);
        if (event == null || event.getDeleted() == 1) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 增加感兴趣计数
        event.setLikes_count(event.getLikes_count() + 1);
        this.updateById(event);

        // 清除缓存
        clearEventCache(eventId);

        log.debug("活动感兴趣计数+1: eventId={}, count={}", eventId, event.getLikes_count());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEvent(Long eventId, Long userId) {
        Events event = this.getById(eventId);
        if (event == null) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 软删除
        event.setDeleted(1);
        event.setUpdated_at(new Date());
        this.updateById(event);

        // 清除缓存
        clearEventCache(eventId);

        log.info("活动删除成功: eventId={}, userId={}", eventId, userId);
    }

    @Override
    public void clearEventCache(Long eventId) {
        // 清除活动详情缓存
        String detailKey = CacheConstants.EVENT_DETAIL_KEY + eventId;
        redisTemplate.delete(detailKey);

        // 清除活动列表缓存（使用通配符删除）
        redisTemplate.delete(redisTemplate.keys(CacheConstants.EVENT_LIST_KEY + "*"));

        // 清除建筑活动计数缓存
        redisTemplate.delete(CacheConstants.BUILDING_EVENT_COUNT_KEY);

        log.debug("活动缓存已清除: eventId={}", eventId);
    }

    /**
     * 异步增加浏览量
     */
    private void incrementViews(Long eventId) {
        try {
            Events event = this.getById(eventId);
            if (event != null) {
                event.setViews_count(event.getViews_count() + 1);
                this.updateById(event);
            }
        } catch (Exception e) {
            log.warn("增加浏览量失败: eventId={}", eventId, e);
        }
    }

    /**
     * 转换为EventCardVO
     */
    private EventCardVO convertToCardVO(Events event) {
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
            Buildings building = buildingsService.getById(Long.parseLong(event.getBuilding_id()));
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        } catch (Exception e) {
            log.warn("获取建筑信息失败: buildingId={}", event.getBuilding_id(), e);
        }
        vo.setRoom(event.getRoom());

        // 获取组织者信息
        Clubs club = clubsService.getById(event.getClub_id());
        if (club != null) {
            vo.setOrganizerName(club.getName());
            vo.setOrganizerLogo(club.getLogo_url());
        }

        vo.setThumbnailUrl(event.getCover_url());
        vo.setInterestedCount(event.getLikes_count());
        vo.setStatus(determineEventStatus(event));

        return vo;
    }

    /**
     * 转换为EventDetailVO
     */
    private EventDetailVO convertToDetailVO(Events event) {
        EventDetailVO vo = new EventDetailVO();
        vo.setId(event.getId());
        vo.setTitle(event.getTitle());
        vo.setSlug(event.getSlug());
        vo.setDescription(event.getDescription_long());

        // 转换时间
        if (event.getStart_time() != null) {
            vo.setStartTime(LocalDateTime.ofInstant(
                event.getStart_time().toInstant(), ZoneId.systemDefault()));
        }
        if (event.getEnd_time() != null) {
            vo.setEndTime(LocalDateTime.ofInstant(
                event.getEnd_time().toInstant(), ZoneId.systemDefault()));
        }

        // 建筑信息
        try {
            Buildings building = buildingsService.getById(Long.parseLong(event.getBuilding_id()));
            if (building != null) {
                vo.setBuildingId(building.getId());
                vo.setBuildingName(building.getName());
                vo.setBuildingAddress(building.getAddress());
            }
        } catch (Exception e) {
            log.warn("获取建筑信息失败: buildingId={}", event.getBuilding_id(), e);
        }
        vo.setRoom(event.getRoom());

        // 组织者信息
        Clubs club = clubsService.getById(event.getClub_id());
        if (club != null) {
            vo.setOrganizerId(club.getId());
            vo.setOrganizerType("CLUB");
            vo.setOrganizerName(club.getName());
            vo.setOrganizerLogo(club.getLogo_url());
            vo.setOrganizerBio(club.getBio());
        }

        vo.setRegistrationUrl(event.getRegistration_notes());
        vo.setThumbnailUrl(event.getCover_url());
        vo.setCoverImageUrl(event.getCover_url());
        vo.setInterestedCount(event.getLikes_count());
        vo.setStatus(determineEventStatus(event));

        // 转换创建/更新时间
        if (event.getCreated_at() != null) {
            vo.setCreatedAt(LocalDateTime.ofInstant(
                event.getCreated_at().toInstant(), ZoneId.systemDefault()));
        }
        if (event.getUpdated_at() != null) {
            vo.setUpdatedAt(LocalDateTime.ofInstant(
                event.getUpdated_at().toInstant(), ZoneId.systemDefault()));
        }

        return vo;
    }

    /**
     * 判断活动状态
     */
    private String determineEventStatus(Events event) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.ofInstant(
            event.getStart_time().toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(
            event.getEnd_time().toInstant(), ZoneId.systemDefault());

        if (now.isBefore(startTime)) {
            return EventConstants.STATUS_UPCOMING;
        } else if (now.isAfter(endTime)) {
            return EventConstants.STATUS_ENDED;
        } else {
            return EventConstants.STATUS_ONGOING;
        }
    }

    /**
     * 生成slug
     */
    private String generateSlug(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");
    }

    /**
     * 构建列表缓存Key
     */
    private String buildListCacheKey(EventFilterDTO filterDTO) {
        return CacheConstants.EVENT_LIST_KEY +
            (filterDTO.getTimeFilter() != null ? filterDTO.getTimeFilter() : "all") + ":" +
            (filterDTO.getBuildingIds() != null ? filterDTO.getBuildingIds().toString() : "all") + ":" +
            filterDTO.getPage() + ":" + filterDTO.getPageSize();
    }
}
