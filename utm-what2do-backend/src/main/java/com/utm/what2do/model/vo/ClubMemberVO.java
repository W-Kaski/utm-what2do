package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object mirroring the club_members table structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubMemberVO {

    private Long id;
    private Long clubId;
    private Long userId;
    private String username;
    private String displayName;
    private String avatar;
    private String role;
    private String status;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
