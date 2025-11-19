package com.utm.what2do.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建筑活动计数气泡VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingCountVO {

    private Long buildingId;

    private String buildingName;

    private String buildingCode;

    private Double latitude;

    private Double longitude;

    private Integer eventCount; // 今日活动数量
}
