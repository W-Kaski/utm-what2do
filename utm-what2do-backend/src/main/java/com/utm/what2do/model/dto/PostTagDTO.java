package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload linking posts to tags.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostTagDTO {

    @NotNull
    private Long postId;

    @NotNull
    private Long tagId;
}
