package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Payload describing a community post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id;

    private Long authorUserId;

    private Long authorClubId;

    @NotBlank
    @Size(max = 500)
    private String content;

    private Boolean pinned;

    private List<Long> tagIds;

    private List<PostMediaDTO> media;
}
