package com.utm.what2do.model.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户状态DTO
 */
@Data
public class UserStatusDTO {

    @NotNull(message = "状态不能为空")
    private Integer deleted;  // 0=解封, 1=禁用
}
