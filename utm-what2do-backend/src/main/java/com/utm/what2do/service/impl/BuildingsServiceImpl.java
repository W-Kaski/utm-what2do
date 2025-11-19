package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.util.DateUtils;
import com.utm.what2do.constant.CacheConstants;
import com.utm.what2do.mapper.BuildingsMapper;
import com.utm.what2do.mapper.EventsMapper;
import com.utm.what2do.model.entity.Buildings;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.model.vo.BuildingCountVO;
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

        // 2. 获取所有建筑
        List<Buildings> buildings = getAllBuildings();

        // 3. 统计今天的活动数量
        LocalDateTime todayStart = DateUtils.getTodayStart();
        LocalDateTime todayEnd = DateUtils.getTodayEnd();
        Date start = Date.from(todayStart.atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(todayEnd.atZone(ZoneId.systemDefault()).toInstant());

        LambdaQueryWrapper<Events> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Events::getDeleted, 0);
        wrapper.between(Events::getStart_time, start, end);
        List<Events> todayEvents = eventsMapper.selectList(wrapper);

        // 4. 按建筑ID分组统计
        Map<String, Long> eventCountMap = todayEvents.stream()
            .collect(Collectors.groupingBy(Events::getBuilding_id, Collectors.counting()));

        // 5. 构建结果
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

        // 6. 缓存到Redis（5分钟）
        redisTemplate.opsForValue().set(cacheKey, result,
            CacheConstants.EVENT_COUNT_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("建筑活动计数统计完成: totalBuildings={}, buildingsWithEvents={}",
            buildings.size(), result.size());

        return result;
    }

    @Override
    public Buildings getBuildingByStringId(String buildingId) {
        return this.getById(buildingId);
    }
}
