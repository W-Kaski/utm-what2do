package com.utm.what2do.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动创建/发布请求DTO
 */
@Data
public class EventCreateDTO {

    @NotBlank(message = "活动标题不能为空")
    private String title;

    private String slug;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "建筑ID不能为空")
    private Long buildingId;

    private String room;

    @NotBlank(message = "活动描述不能为空")
    private String description;

    @NotNull(message = "组织者ID不能为空")
    private Long organizerId;

    private String organizerType; // 默认为 "CLUB"

    private String registrationUrl;

    private String thumbnailUrl;

    private String coverImageUrl;

    private Integer capacity;

    private List<String> tags; // 活动标签列表
}
