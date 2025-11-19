package com.utm.what2do.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社团详情展示VO
 */
@Data
public class ClubDetailVO {

    private Long id;

    private String name;

    private String slug;

    private String bio;

    private String logoUrl;

    private String coverImageUrl;

    private String websiteUrl;

    private String instagramUrl;

    private String facebookUrl;

    private String twitterUrl;

    private String email;

    private Integer followersCount;

    private LocalDateTime createdAt;

    /**
     * 即将举办的活动列表
     */
    private List<EventCardVO> upcomingEvents;
}
