package com.utm.what2do.task;

import com.utm.what2do.service.BuildingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 缓存刷新定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheRefreshTask {

    private final BuildingsService buildingsService;

    /**
     * 每天凌晨0点刷新建筑活动计数缓存
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshBuildingEventCountAtMidnight() {
        log.info("定时任务: 刷新建筑活动计数缓存");
        try {
            buildingsService.refreshBuildingEventCountCache();
            log.info("定时任务: 建筑活动计数缓存刷新完成");
        } catch (Exception e) {
            log.error("定时任务: 刷新建筑活动计数缓存失败", e);
        }
    }

    /**
     * 每小时刷新一次建筑活动计数（可选，确保数据较新）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshBuildingEventCountHourly() {
        log.debug("定时任务: 每小时刷新建筑活动计数缓存");
        try {
            buildingsService.refreshBuildingEventCountCache();
        } catch (Exception e) {
            log.error("定时任务: 每小时刷新建筑活动计数缓存失败", e);
        }
    }
}
