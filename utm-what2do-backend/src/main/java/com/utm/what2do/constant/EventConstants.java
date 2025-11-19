package com.utm.what2do.constant;

/**
 * 活动相关常量
 */
public class EventConstants {

    /**
     * 活动状态 - 即将举行
     */
    public static final String STATUS_UPCOMING = "UPCOMING";

    /**
     * 活动状态 - 进行中
     */
    public static final String STATUS_ONGOING = "ONGOING";

    /**
     * 活动状态 - 已结束
     */
    public static final String STATUS_ENDED = "ENDED";

    /**
     * 活动状态 - 已取消
     */
    public static final String STATUS_CANCELLED = "CANCELLED";

    /**
     * 时间筛选 - 今天
     */
    public static final String TIME_FILTER_TODAY = "TODAY";

    /**
     * 时间筛选 - 本周
     */
    public static final String TIME_FILTER_WEEK = "WEEK";

    /**
     * 时间筛选 - 本月
     */
    public static final String TIME_FILTER_MONTH = "MONTH";

    /**
     * 排序方式 - 按时间升序（最近的活动优先）
     */
    public static final String SORT_BY_TIME_ASC = "time_asc";

    /**
     * 排序方式 - 按时间降序
     */
    public static final String SORT_BY_TIME_DESC = "time_desc";

    /**
     * 排序方式 - 按热度（感兴趣人数）
     */
    public static final String SORT_BY_POPULARITY = "popularity";

    /**
     * 排序方式 - 按创建时间
     */
    public static final String SORT_BY_CREATED = "created";
}
