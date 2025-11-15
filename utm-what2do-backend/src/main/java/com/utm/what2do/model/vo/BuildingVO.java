package com.utm.what2do.model.vo;

import com.utm.what2do.model.enums.BuildingCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object describing a campus building.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingVO {

    private String id;
    private String name;
    private BigDecimal lat;
    private BigDecimal lng;
    private String campusZone;
    private BuildingCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
