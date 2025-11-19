package com.utm.what2do.service;

import com.utm.what2do.model.entity.Buildings;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.BuildingCountVO;

import java.util.List;

/**
* @author PC
* @description 针对表【buildings】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface BuildingsService extends IService<Buildings> {

    /**
     * 获取所有建筑列表（使用Caffeine缓存）
     * @return 建筑列表
     */
    List<Buildings> getAllBuildings();

    /**
     * 获取建筑活动计数（用于地图气泡）
     * @return 建筑活动计数列表
     */
    List<BuildingCountVO> getBuildingEventCounts();

    /**
     * 根据ID获取建筑（字符串ID）
     * @param buildingId 建筑ID
     * @return 建筑信息
     */
    Buildings getBuildingByStringId(String buildingId);
}
