package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object returned for tag suggestions/listing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagVO {

    private Long id;
    private String name;
    private String kind;
    private LocalDateTime createdAt;
}
