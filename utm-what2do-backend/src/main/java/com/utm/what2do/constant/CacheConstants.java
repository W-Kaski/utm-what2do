package com.utm.what2do.constant;

/**
 * 缓存Key常量
 */
public class CacheConstants {

    /**
     * 建筑信息缓存 Key 前缀
     */
    public static final String BUILDING_KEY = "building:";

    /**
     * 建筑列表缓存 Key
     */
    public static final String BUILDINGS_LIST_KEY = "buildings:list";

    /**
     * 建筑活动计数缓存 Key
     */
    public static final String BUILDING_EVENT_COUNT_KEY = "building:event:count";

    /**
     * 活动详情缓存 Key 前缀
     */
    public static final String EVENT_DETAIL_KEY = "event:detail:";

    /**
     * 活动列表缓存 Key 前缀
     */
    public static final String EVENT_LIST_KEY = "event:list:";

    /**
     * 热门活动缓存 Key
     */
    public static final String HOT_EVENTS_KEY = "events:hot";

    /**
     * 社团详情缓存 Key 前缀
     */
    public static final String CLUB_DETAIL_KEY = "club:detail:";

    /**
     * 社团列表缓存 Key
     */
    public static final String CLUBS_LIST_KEY = "clubs:list";

    /**
     * 用户信息缓存 Key 前缀
     */
    public static final String USER_INFO_KEY = "user:info:";

    /**
     * 帖子详情缓存 Key 前缀
     */
    public static final String POST_DETAIL_KEY = "post:detail:";

    /**
     * 标签缓存 Key
     */
    public static final String TAGS_KEY = "tags:";

    /**
     * 缓存过期时间（秒）
     */
    public static final long CACHE_EXPIRE_TIME = 3600L; // 1小时

    /**
     * 建筑信息缓存过期时间（秒）- 静态数据，较长时间
     */
    public static final long BUILDING_CACHE_EXPIRE_TIME = 86400L; // 24小时

    /**
     * 活动计数缓存过期时间（秒）- 频繁变化，较短时间
     */
    public static final long EVENT_COUNT_CACHE_EXPIRE_TIME = 300L; // 5分钟
}
