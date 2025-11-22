package com.utm.what2do.model.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 社团经理管理DTO
 */
@Data
public class ClubManagerDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
