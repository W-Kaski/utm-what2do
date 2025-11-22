package com.utm.what2do.controller;

import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.model.vo.TagVO;
import com.utm.what2do.service.TagsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理Controller
 */
@Tag(name = "标签管理", description = "活动标签相关API")
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagsService tagsService;

    /**
     * 获取活动标签列表
     */
    @Operation(summary = "获取活动标签", description = "获取所有活动标签（使用Caffeine缓存）")
    @GetMapping("/event")
    public ResultVO<List<TagVO>> getEventTags() {
        List<TagVO> tags = tagsService.getEventTags();
        return ResultVO.success(tags);
    }
}
