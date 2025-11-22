package com.utm.what2do.controller;

import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.model.vo.BuildingCountVO;
import com.utm.what2do.model.vo.BuildingVO;
import com.utm.what2do.service.BuildingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地图/建筑管理Controller
 */
@Tag(name = "地图管理", description = "建筑信息、地图活动计数气泡等API")
@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class MapController {

    private final BuildingsService buildingsService;

    /**
     * 获取所有建筑列表
     */
    @Operation(summary = "获取建筑列表", description = "获取UTM所有建筑的名称、坐标和ID（使用Caffeine缓存）")
    @GetMapping
    public ResultVO<List<BuildingVO>> getAllBuildings() {
        List<BuildingVO> buildings = buildingsService.getAllBuildingsVO();
        return ResultVO.success(buildings);
    }

    /**
     * 获取建筑详情
     */
    @Operation(summary = "获取建筑详情", description = "根据建筑ID获取详细信息（使用Caffeine缓存）")
    @GetMapping("/{id}")
    public ResultVO<BuildingVO> getBuildingDetail(@PathVariable String id) {
        BuildingVO building = buildingsService.getBuildingDetail(id);
        return ResultVO.success(building);
    }

    /**
     * 获取建筑活动计数（地图气泡数据）
     */
    @Operation(summary = "获取建筑活动计数", description = "获取每个建筑今日活动数量（用于地图气泡显示，使用Redis缓存）")
    @GetMapping("/counts")
    public ResultVO<List<BuildingCountVO>> getBuildingEventCounts() {
        List<BuildingCountVO> counts = buildingsService.getBuildingEventCounts();
        return ResultVO.success(counts);
    }
}
