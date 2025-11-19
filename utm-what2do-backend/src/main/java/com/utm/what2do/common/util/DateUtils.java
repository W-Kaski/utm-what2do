package com.utm.what2do.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期时间工具类
 */
public class DateUtils {

    /**
     * 获取今天的开始时间（00:00:00）
     */
    public static LocalDateTime getTodayStart() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 获取今天的结束时间（23:59:59）
     */
    public static LocalDateTime getTodayEnd() {
        return LocalDate.now().atTime(23, 59, 59);
    }

    /**
     * 获取本周的开始时间（周一 00:00:00）
     */
    public static LocalDateTime getWeekStart() {
        return LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                .atStartOfDay();
    }

    /**
     * 获取本周的结束时间（周日 23:59:59）
     */
    public static LocalDateTime getWeekEnd() {
        return LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
                .atTime(23, 59, 59);
    }

    /**
     * 获取本月的开始时间（1号 00:00:00）
     */
    public static LocalDateTime getMonthStart() {
        return LocalDate.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();
    }

    /**
     * 获取本月的结束时间（最后一天 23:59:59）
     */
    public static LocalDateTime getMonthEnd() {
        return LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);
    }

    /**
     * 判断给定时间是否在今天
     */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate date = dateTime.toLocalDate();
        return today.equals(date);
    }

    /**
     * 判断给定时间是否在本周
     */
    public static boolean isThisWeek(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime weekStart = getWeekStart();
        LocalDateTime weekEnd = getWeekEnd();
        return !dateTime.isBefore(weekStart) && !dateTime.isAfter(weekEnd);
    }

    /**
     * 判断给定时间是否在本月
     */
    public static boolean isThisMonth(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime monthStart = getMonthStart();
        LocalDateTime monthEnd = getMonthEnd();
        return !dateTime.isBefore(monthStart) && !dateTime.isAfter(monthEnd);
    }

    /**
     * 判断给定时间是否是未来时间
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * 判断给定时间是否是过去时间
     */
    public static boolean isPast(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isBefore(LocalDateTime.now());
    }
}
