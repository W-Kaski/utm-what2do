package com.utm.what2do.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.utm.what2do.annotation.CheckRole;
import com.utm.what2do.common.response.ResultVO;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.model.dto.ClubUpdateDTO;
import com.utm.what2do.model.entity.Clubs;
import com.utm.what2do.model.vo.ClubDetailVO;
import com.utm.what2do.model.vo.ClubMemberVO;
import com.utm.what2do.service.ClubsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社团管理Controller
 */
@Tag(name = "社团管理", description = "社团列表、社团详情等API")
@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubsService clubsService;

    /**
     * 获取所有社团列表
     */
    @Operation(summary = "获取社团列表", description = "获取UTM所有社团信息，按成员数量排序")
    @GetMapping
    public ResultVO<List<Clubs>> getAllClubs() {
        List<Clubs> clubs = clubsService.getAllClubs();
        return ResultVO.success(clubs);
    }

    /**
     * 根据slug获取社团详情
     */
    @Operation(summary = "获取社团详情", description = "根据社团slug获取详细信息（包含即将举办的活动）")
    @GetMapping("/{slug}")
    public ResultVO<ClubDetailVO> getClubDetailBySlug(@PathVariable String slug) {
        ClubDetailVO detail = clubsService.getClubDetailBySlug(slug);
        return ResultVO.success(detail);
    }

    /**
     * 根据ID获取社团详情
     */
    @Operation(summary = "根据ID获取社团详情", description = "根据社团ID获取详细信息")
    @GetMapping("/id/{id}")
    public ResultVO<ClubDetailVO> getClubDetailById(@PathVariable Long id) {
        ClubDetailVO detail = clubsService.getClubDetail(id);
        return ResultVO.success(detail);
    }

    /**
     * 更新社团信息
     */
    @Operation(summary = "更新社团信息", description = "社团管理员更新社团信息")
    @CheckRole({RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @PutMapping("/id/{id}")
    public ResultVO<ClubDetailVO> updateClub(
            @PathVariable Long id,
            @Valid @RequestBody ClubUpdateDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        ClubDetailVO detail = clubsService.updateClub(id, dto, userId);
        return ResultVO.success("社团信息更新成功", detail);
    }

    /**
     * 获取社团成员列表
     */
    @Operation(summary = "获取社团成员", description = "获取社团成员列表（需要MANAGER或ADMIN权限）")
    @CheckRole({RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN})
    @GetMapping("/id/{id}/members")
    public ResultVO<List<ClubMemberVO>> getClubMembers(
            @PathVariable Long id,
            @RequestParam(required = false) String role) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ClubMemberVO> members = clubsService.getClubMembers(id, role, userId);
        return ResultVO.success(members);
    }
}
