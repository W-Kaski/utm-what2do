package com.utm.what2do.service;

import com.utm.what2do.model.entity.Buildings;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.BuildingCountVO;
import com.utm.what2do.model.vo.BuildingVO;

import java.util.List;

/**
* @author PC
* @description 针对表【buildings】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface BuildingsService extends IService<Buildings> {

    /**
     * 获取所有建筑列表（使用Caffeine缓存）
     * @return 建筑VO列表
     */
    List<BuildingVO> getAllBuildingsVO();

    /**
     * 获取所有建筑实体列表（内部使用）
     * @return 建筑实体列表
     */
    List<Buildings> getAllBuildings();

    /**
     * 获取建筑活动计数（用于地图气泡，使用Redis缓存）
     * @return 建筑活动计数列表
     */
    List<BuildingCountVO> getBuildingEventCounts();

    /**
     * 刷新建筑活动计数缓存
     */
    void refreshBuildingEventCountCache();

    /**
     * 根据ID获取建筑详情（使用Caffeine缓存）
     * @param buildingId 建筑ID
     * @return 建筑VO
     */
    BuildingVO getBuildingDetail(String buildingId);

    /**
     * 根据ID获取建筑实体
     * @param buildingId 建筑ID
     * @return 建筑实体
     */
    Buildings getBuildingByStringId(String buildingId);
}
