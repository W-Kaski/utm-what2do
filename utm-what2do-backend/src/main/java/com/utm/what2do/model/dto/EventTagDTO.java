package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload binding tags to events.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTagDTO {

    @NotNull
    private Long eventId;

    @NotNull
    private Long tagId;
}
