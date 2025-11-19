package com.utm.what2do.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动卡片展示VO（用于列表）
 */
@Data
public class EventCardVO {

    private Long id;

    private String title;

    private String slug;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String buildingName;

    private String room;

    private String organizerName;

    private String organizerLogo;

    private String thumbnailUrl;

    private Integer interestedCount;

    private List<String> tags;

    private String status; // UPCOMING, ONGOING, ENDED, CANCELLED
}
