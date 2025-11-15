package com.utm.what2do.model.dto;

import com.utm.what2do.model.enums.ClubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload used to create or update a club.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubDTO {

    private Long id;

    @NotBlank
    @Size(max = 180)
    private String name;

    @NotBlank
    @Size(max = 120)
    private String slug;

    @Size(max = 255)
    private String tagline;

    private String description;

    private ClubCategory category;

    private String logoUrl;

    private String coverUrl;
}
