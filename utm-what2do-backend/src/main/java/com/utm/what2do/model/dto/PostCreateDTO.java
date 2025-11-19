package com.utm.what2do.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 帖子发布请求DTO
 */
@Data
public class PostCreateDTO {

    @NotBlank(message = "帖子内容不能为空")
    private String content;

    private String authorType; // USER 或 CLUB

    private List<String> mediaUrls; // 媒体资源URL列表

    private List<String> tags; // 帖子标签
}
