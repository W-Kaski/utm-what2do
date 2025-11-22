package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.common.util.DateUtils;
import com.utm.what2do.constant.CacheConstants;
import com.utm.what2do.mapper.BuildingsMapper;
import com.utm.what2do.mapper.EventsMapper;
import com.utm.what2do.model.entity.Buildings;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.vo.BuildingCountVO;
import com.utm.what2do.model.vo.BuildingVO;
import com.utm.what2do.service.BuildingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author PC
* @description 针对表【buildings】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingsServiceImpl extends ServiceImpl<BuildingsMapper, Buildings>
    implements BuildingsService{

    private final RedisTemplate<String, Object> redisTemplate;
    private final EventsMapper eventsMapper;

    @Override
    @Cacheable(value = "buildings", key = "'all_vo'")
    public List<BuildingVO> getAllBuildingsVO() {
        List<Buildings> buildings = getAllBuildings();
        return buildings.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "buildings", key = "'all'")
    public List<Buildings> getAllBuildings() {
        LambdaQueryWrapper<Buildings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Buildings::getDeleted, 0);
        wrapper.orderByAsc(Buildings::getName);

        List<Buildings> buildings = this.list(wrapper);
        log.info("查询所有建筑: count={}", buildings.size());

        return buildings;
    }

    @Override
    public List<BuildingCountVO> getBuildingEventCounts() {
        // 1. 尝试从Redis缓存读取
        String cacheKey = CacheConstants.BUILDING_EVENT_COUNT_KEY;
        @SuppressWarnings("unchecked")
        List<BuildingCountVO> cachedCounts = (List<BuildingCountVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedCounts != null) {
            log.debug("从Redis缓存读取建筑活动计数");
            return cachedCounts;
        }

        // 2. 缓存未命中，重新计算
        return calculateAndCacheBuildingEventCounts();
    }

    @Override
    public void refreshBuildingEventCountCache() {
        log.info("开始刷新建筑活动计数缓存");
        calculateAndCacheBuildingEventCounts();
    }

    /**
     * 计算并缓存建筑活动计数
     */
    private List<BuildingCountVO> calculateAndCacheBuildingEventCounts() {
        // 1. 获取所有建筑
        List<Buildings> buildings = getAllBuildings();

        // 2. 统计今天的活动数量
        LocalDateTime todayStart = DateUtils.getTodayStart();
        LocalDateTime todayEnd = DateUtils.getTodayEnd();
        Date start = Date.from(todayStart.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(todayEnd.atZone(ZoneId.systemDefault()).toInstant());

        LambdaQueryWrapper<Events> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Events::getDeleted, 0);
        wrapper.between(Events::getStart_time, start, end);
        List<Events> todayEvents = eventsMapper.selectList(wrapper);

        // 3. 按建筑ID分组统计
        Map<String, Long> eventCountMap = todayEvents.stream()
            .filter(e -> e.getBuilding_id() != null)
            .collect(Collectors.groupingBy(Events::getBuilding_id, Collectors.counting()));

        // 4. 构建结果
        List<BuildingCountVO> result = buildings.stream()
            .map(building -> {
                BuildingCountVO vo = new BuildingCountVO();
                vo.setBuildingId(Long.parseLong(building.getId()));
                vo.setBuildingName(building.getName());
                vo.setBuildingCode(building.getId());
                vo.setLatitude(building.getLat() != null ? building.getLat().doubleValue() : null);
                vo.setLongitude(building.getLng() != null ? building.getLng().doubleValue() : null);
                vo.setEventCount(eventCountMap.getOrDefault(building.getId(), 0L).intValue());
                return vo;
            })
            .filter(vo -> vo.getEventCount() > 0) // 只返回有活动的建筑
            .collect(Collectors.toList());

        // 5. 缓存到Redis（到今日结束或24小时）
        String cacheKey = CacheConstants.BUILDING_EVENT_COUNT_KEY;
        redisTemplate.opsForValue().set(cacheKey, result,
            CacheConstants.EVENT_COUNT_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("建筑活动计数统计完成: totalBuildings={}, buildingsWithEvents={}",
            buildings.size(), result.size());

        return result;
    }

    @Override
    @Cacheable(value = "buildings", key = "#buildingId")
    public BuildingVO getBuildingDetail(String buildingId) {
        Buildings building = this.getById(buildingId);
        if (building == null || building.getDeleted() == 1) {
            throw new BusinessException(StatusCode.NOT_FOUND.getCode(), "建筑不存在");
        }

        log.debug("查询建筑详情: buildingId={}", buildingId);
        return convertToVO(building);
    }

    @Override
    public Buildings getBuildingByStringId(String buildingId) {
        Buildings building = this.getById(buildingId);
        if (building == null || building.getDeleted() == 1) {
            throw new BusinessException(StatusCode.NOT_FOUND.getCode(), "建筑不存在");
        }
        return building;
    }

    /**
     * 将Buildings实体转换为BuildingVO
     */
    private BuildingVO convertToVO(Buildings building) {
        return BuildingVO.builder()
            .id(building.getId())
            .name(building.getName())
            .lat(building.getLat())
            .lng(building.getLng())
            .campusZone(building.getCampus_zone())
            .category(building.getCategory())
            .createdAt(building.getCreated_at() != null ?
                LocalDateTime.ofInstant(building.getCreated_at().toInstant(), ZoneId.systemDefault()) : null)
            .updatedAt(building.getUpdated_at() != null ?
                LocalDateTime.ofInstant(building.getUpdated_at().toInstant(), ZoneId.systemDefault()) : null)
            .build();
    }
}
