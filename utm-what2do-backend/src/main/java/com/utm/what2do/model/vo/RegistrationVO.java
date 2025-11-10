package com.utm.what2do.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationVO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long userId;
    private String userName;
    private String status;
    private LocalDateTime registeredAt;
    private String remarks;
}
