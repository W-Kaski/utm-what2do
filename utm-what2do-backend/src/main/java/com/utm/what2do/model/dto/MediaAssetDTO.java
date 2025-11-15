package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload representing an uploaded media asset.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAssetDTO {

    private Long id;

    @NotBlank
    private String hash;

    @NotBlank
    private String url;

    @NotBlank
    private String mimeType;

    @NotNull
    private Long sizeBytes;

    private Integer width;

    private Integer height;
}
