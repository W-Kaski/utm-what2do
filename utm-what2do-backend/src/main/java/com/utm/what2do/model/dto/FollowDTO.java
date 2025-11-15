package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic payload for follow/unfollow actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {

    @NotNull
    private Long followerUserId;

    @NotNull
    private Long targetId;
}
