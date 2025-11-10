package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityRequestDTO {

    @NotNull
    private Long organizationId;

    @NotBlank
    private String title;

    private String summary;

    private String location;

    @NotNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer capacity;

    private LocalDateTime registrationDeadline;
}
