package com.utm.what2do.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object representing an event card or detail page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventVO {

    private Long id;
    private String title;
    private String slug;
    private String descriptionLong;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String buildingId;
    private String room;
    private Long clubId;
    private String coverUrl;
    private Boolean official;
    private String registrationNotes;
    private Integer viewsCount;
    private Integer likesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
