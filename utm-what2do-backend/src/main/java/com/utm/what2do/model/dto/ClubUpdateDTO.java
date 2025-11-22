package com.utm.what2do.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新社团信息DTO
 */
@Data
public class ClubUpdateDTO {

    @Size(max = 180, message = "社团名称不能超过180字符")
    private String name;

    @Size(max = 120, message = "Slug不能超过120字符")
    private String slug;

    @Size(max = 255, message = "Tagline不能超过255字符")
    private String tagline;

    private String description;

    private Integer category;

    private String logoUrl;

    private String coverUrl;
}
