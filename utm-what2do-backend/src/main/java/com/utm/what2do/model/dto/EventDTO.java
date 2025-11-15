package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Event level payload exchanged with the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    private String descriptionLong;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private String buildingId;

    private String room;

    private Long clubId;

    private String coverUrl;

    private Boolean official;

    private String registrationNotes;

    /**
     * Helper for binding tags during creation.
     */
    private List<Long> tagIds;
}
