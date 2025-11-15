package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event to tag relation returned to clients when needed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTagVO {

    private Long id;
    private Long eventId;
    private Long tagId;
    private LocalDateTime createdAt;
}
