package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload describing media attached to a post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMediaDTO {

    private Long id;

    @NotNull
    private Long postId;

    @NotBlank
    private String type;

    @NotBlank
    private String url;

    private Integer orderIndex;
}
