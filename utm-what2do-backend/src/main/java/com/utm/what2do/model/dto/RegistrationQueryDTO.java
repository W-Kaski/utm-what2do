package com.utm.what2do.model.dto;

import lombok.Data;

@Data
public class RegistrationQueryDTO {
    private int page = 1;
    private int size = 10;
    private Long userId;
    private Long activityId;
    private String status;
}
