package com.utm.what2do.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动详情展示VO
 */
@Data
public class EventDetailVO {

    private Long id;

    private String title;

    private String slug;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long buildingId;

    private String buildingName;

    private String buildingAddress;

    private String room;

    private String description;

    private Long organizerId;

    private String organizerType;

    private String organizerName;

    private String organizerLogo;

    private String organizerBio;

    private String registrationUrl;

    private String thumbnailUrl;

    private String coverImageUrl;

    private Integer capacity;

    private Integer interestedCount;

    private List<String> tags;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
