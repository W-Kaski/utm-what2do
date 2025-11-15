package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload used to create or update a tag.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {

    private Long id;

    @NotBlank
    @Size(max = 80)
    private String name;

    /**
     * GENERAL / HASHTAG / SYSTEM
     */
    private String kind;
}
