package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.dto.EventCreateDTO;
import com.utm.what2do.model.dto.EventFilterDTO;
import com.utm.what2do.model.entity.Events;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.model.vo.EventDetailVO;

/**
* @author PC
* @description 针对表【events】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface EventsService extends IService<Events> {

    /**
     * 创建活动
     * @param dto 活动创建信息
     * @param userId 创建者ID
     * @return 活动详情
     */
    EventDetailVO createEvent(EventCreateDTO dto, Long userId);

    /**
     * 获取活动列表（支持筛选）
     * @param filterDTO 筛选条件
     * @return 分页的活动卡片列表
     */
    Page<EventCardVO> getEventList(EventFilterDTO filterDTO);

    /**
     * 获取活动详情
     * @param eventId 活动ID
     * @return 活动详情
     */
    EventDetailVO getEventDetail(Long eventId);

    /**
     * 更新活动
     * @param eventId 活动ID
     * @param dto 更新信息
     * @param userId 更新者ID
     * @return 更新后的活动详情
     */
    EventDetailVO updateEvent(Long eventId, EventCreateDTO dto, Long userId);

    /**
     * 增加感兴趣计数
     * @param eventId 活动ID
     */
    void incrementInterested(Long eventId);

    /**
     * 删除活动（软删除）
     * @param eventId 活动ID
     * @param userId 操作者ID
     */
    void deleteEvent(Long eventId, Long userId);

    /**
     * 清除活动相关缓存
     * @param eventId 活动ID
     */
    void clearEventCache(Long eventId);
}
