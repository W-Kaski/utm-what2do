package com.utm.what2do.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityVO {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private String title;
    private String summary;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private LocalDateTime registrationDeadline;
}
