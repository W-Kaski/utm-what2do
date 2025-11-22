package com.utm.what2do.model.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改用户角色DTO
 */
@Data
public class UserRoleDTO {

    @NotBlank(message = "角色不能为空")
    private String role;
}
