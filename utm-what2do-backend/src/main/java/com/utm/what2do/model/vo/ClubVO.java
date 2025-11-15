package com.utm.what2do.model.vo;

import com.utm.what2do.model.enums.ClubCategory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object for club information exposed to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubVO {

    private Long id;
    private String name;
    private String slug;
    private String tagline;
    private String description;
    private ClubCategory category;
    private String logoUrl;
    private String coverUrl;
    private Integer membersCount;
    private Integer eventsCount;
    private Integer postsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
