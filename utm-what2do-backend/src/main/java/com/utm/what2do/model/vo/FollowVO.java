package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View of a follow relation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowVO {

    private Long id;
    private Long followerUserId;
    private Long targetId;
    private LocalDateTime createdAt;
}
