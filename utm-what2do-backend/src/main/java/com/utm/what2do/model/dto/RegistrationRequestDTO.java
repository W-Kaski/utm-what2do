package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationRequestDTO {

    @NotNull
    private Long activityId;

    @NotNull
    private Long userId;

    @NotBlank
    private String status;

    private String remarks;
}
