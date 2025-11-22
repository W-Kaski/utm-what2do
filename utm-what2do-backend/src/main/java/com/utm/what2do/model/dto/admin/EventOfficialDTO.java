package com.utm.what2do.model.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动官方标记DTO
 */
@Data
public class EventOfficialDTO {

    @NotNull(message = "官方标记不能为空")
    private Boolean isOfficial;
}
