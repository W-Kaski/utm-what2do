package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload describing a club member relationship.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubMemberDTO {

    private Long id;

    @NotNull
    private Long clubId;

    @NotNull
    private Long userId;

    @NotBlank
    private String role;

    @NotBlank
    private String status;
}
