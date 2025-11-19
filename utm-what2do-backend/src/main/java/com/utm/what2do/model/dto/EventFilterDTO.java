package com.utm.what2do.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 活动筛选请求DTO
 */
@Data
public class EventFilterDTO {

    /**
     * 时间筛选: TODAY, WEEK, MONTH
     */
    private String timeFilter;

    /**
     * 建筑ID列表
     */
    private List<Long> buildingIds;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 排序方式: time_asc, time_desc, popularity, created
     */
    private String sortBy;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;
}
