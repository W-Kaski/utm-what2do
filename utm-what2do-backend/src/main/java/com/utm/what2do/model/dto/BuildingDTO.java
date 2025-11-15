package com.utm.what2do.model.dto;

import com.utm.what2do.model.enums.BuildingCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payload describing a campus building.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal lat;

    @NotNull
    private BigDecimal lng;

    private String campusZone;

    private BuildingCategory category;
}
