package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.dto.EventCreateDTO;
import com.utm.what2do.model.dto.EventFilterDTO;
import com.utm.what2do.model.vo.EventCardVO;
import com.utm.what2do.model.vo.EventDetailVO;
import com.utm.what2do.service.EventsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 活动管理Controller
 */
@Tag(name = "活动管理", description = "活动列表、详情、发布、筛选等API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventsService eventsService;

    /**
     * 获取活动列表（支持筛选）
     */
    @Operation(summary = "获取活动列表", description = "支持按时间、建筑、关键词筛选和排序")
    @GetMapping
    public ResultVO<Page<EventCardVO>> getEventList(EventFilterDTO filterDTO) {
        Page<EventCardVO> page = eventsService.getEventList(filterDTO);
        return ResultVO.success(page);
    }

    /**
     * 获取活动详情
     */
    @Operation(summary = "获取活动详情", description = "根据活动ID获取完整的活动信息")
    @GetMapping("/{id}")
    public ResultVO<EventDetailVO> getEventDetail(@PathVariable Long id) {
        EventDetailVO detail = eventsService.getEventDetail(id);
        return ResultVO.success(detail);
    }

    /**
     * 发布新活动
     */
    @Operation(summary = "发布新活动", description = "社团管理员发布新活动")
    @CheckRole({RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping
    public ResultVO<EventDetailVO> createEvent(@Valid @RequestBody EventCreateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        EventDetailVO event = eventsService.createEvent(dto, userId);
        return ResultVO.success("活动发布成功", event);
    }

    /**
     * 更新活动
     */
    @Operation(summary = "更新活动", description = "社团管理员修改活动信息")
    @CheckRole({RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PutMapping("/{id}")
    public ResultVO<EventDetailVO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventCreateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        EventDetailVO updated = eventsService.updateEvent(id, dto, userId);
        return ResultVO.success("活动更新成功", updated);
    }

    /**
     * 删除活动
     */
    @Operation(summary = "删除活动", description = "社团管理员删除活动（软删除）")
    @CheckRole({RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @DeleteMapping("/{id}")
    public ResultVO<Void> deleteEvent(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        eventsService.deleteEvent(id, userId);
        return ResultVO.success("活动删除成功");
    }

    /**
     * 标记感兴趣
     */
    @Operation(summary = "标记感兴趣", description = "增加活动的感兴趣计数")
    @CheckRole({RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PostMapping("/{id}/interested")
    public ResultVO<Void> markInterested(@PathVariable Long id) {
        eventsService.incrementInterested(id);
        return ResultVO.success("标记成功");
    }
}
