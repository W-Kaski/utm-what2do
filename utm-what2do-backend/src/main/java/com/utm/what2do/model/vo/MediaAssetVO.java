package com.utm.what2do.model.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View object for uploaded media metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAssetVO {

    private Long id;
    private String hash;
    private String url;
    private String mimeType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private LocalDateTime createdAt;
}
