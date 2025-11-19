package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.FollowsMapper;
import com.utm.what2do.model.entity.Clubs;
import com.utm.what2do.model.entity.Follows;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.ClubDetailVO;
import com.utm.what2do.service.ClubsService;
import com.utm.what2do.service.FollowsService;
import com.utm.what2do.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 关注Service实现（关注社团）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowsServiceImpl extends ServiceImpl<FollowsMapper, Follows>
    implements FollowsService {

    private final ClubsService clubsService;
    private final UsersService usersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followClub(Long userId, Long clubId) {
        // 验证社团存在
        Clubs club = clubsService.getById(clubId);
        if (club == null) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 检查是否已关注
        if (isFollowingClub(userId, clubId)) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "已关注该社团");
        }

        // 创建关注关系
        Follows follow = new Follows();
        follow.setFollower_user_id(userId);
        follow.setTarget_id(clubId);
        follow.setCreated_at(new Date());

        boolean saved = this.save(follow);
        if (!saved) {
            throw new BusinessException(500, "关注失败");
        }

        // 更新用户关注数
        Users user = usersService.getById(userId);
        if (user != null) {
            user.setFollowing_count(user.getFollowing_count() + 1);
            usersService.updateById(user);
        }

        log.info("关注社团成功: userId={}, clubId={}", userId, clubId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowClub(Long userId, Long clubId) {
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, userId)
               .eq(Follows::getTarget_id, clubId);

        Follows follow = this.getOne(wrapper);
        if (follow == null) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "未关注该社团");
        }

        this.removeById(follow.getId());

        // 更新用户关注数
        Users user = usersService.getById(userId);
        if (user != null && user.getFollowing_count() > 0) {
            user.setFollowing_count(user.getFollowing_count() - 1);
            usersService.updateById(user);
        }

        log.info("取消关注社团成功: userId={}, clubId={}", userId, clubId);
    }

    @Override
    public boolean isFollowingClub(Long userId, Long clubId) {
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, userId)
               .eq(Follows::getTarget_id, clubId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<ClubDetailVO> getFollowingClubs(Long userId, Long current, Long size) {
        Page<Follows> page = new Page<>(current, size);
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, userId)
               .orderByDesc(Follows::getCreated_at);

        Page<Follows> followPage = this.page(page, wrapper);

        Page<ClubDetailVO> voPage = new Page<>(current, size);
        voPage.setTotal(followPage.getTotal());
        voPage.setRecords(followPage.getRecords().stream()
            .map(follow -> {
                try {
                    return clubsService.getClubDetail(follow.getTarget_id());
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(vo -> vo != null)
            .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Long getFollowingCount(Long userId) {
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, userId);
        return this.count(wrapper);
    }

    @Override
    public Long getClubFollowerCount(Long clubId) {
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getTarget_id, clubId);
        return this.count(wrapper);
    }
}
