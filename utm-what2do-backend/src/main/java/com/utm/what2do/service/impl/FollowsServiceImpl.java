package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.mapper.FollowsMapper;
import com.utm.what2do.model.entity.Follows;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.FollowsService;
import com.utm.what2do.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 关注Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowsServiceImpl extends ServiceImpl<FollowsMapper, Follows>
    implements FollowsService {

    private final UsersService usersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followUser(Long followerId, Long targetUserId) {
        // 不能关注自己
        if (followerId.equals(targetUserId)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "不能关注自己");
        }

        // 验证目标用户存在
        Users targetUser = usersService.getById(targetUserId);
        if (targetUser == null || targetUser.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 检查是否已关注
        if (isFollowing(followerId, targetUserId)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "已经关注该用户");
        }

        // 创建关注关系
        Follows follow = new Follows();
        follow.setFollower_user_id(followerId);
        follow.setTarget_id(targetUserId);
        follow.setCreated_at(new Date());

        boolean saved = this.save(follow);
        if (!saved) {
            throw new BusinessException(500, "关注失败");
        }

        // 更新关注数
        Users follower = usersService.getById(followerId);
        if (follower != null) {
            follower.setFollowing_count(follower.getFollowing_count() + 1);
            usersService.updateById(follower);
        }

        log.info("关注成功: followerId={}, targetUserId={}", followerId, targetUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long followerId, Long targetUserId) {
        // 查找关注关系
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, followerId)
               .eq(Follows::getTarget_id, targetUserId);

        Follows follow = this.getOne(wrapper);
        if (follow == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "未关注该用户");
        }

        // 删除关注关系
        this.removeById(follow.getId());

        // 更新关注数
        Users follower = usersService.getById(followerId);
        if (follower != null && follower.getFollowing_count() > 0) {
            follower.setFollowing_count(follower.getFollowing_count() - 1);
            usersService.updateById(follower);
        }

        log.info("取消关注成功: followerId={}, targetUserId={}", followerId, targetUserId);
    }

    @Override
    public boolean isFollowing(Long followerId, Long targetUserId) {
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, followerId)
               .eq(Follows::getTarget_id, targetUserId);
        return this.count(wrapper) > 0;
    }

    @Override
    public Page<UserInfoVO> getFollowingList(Long userId, Long current, Long size) {
        Page<Follows> page = new Page<>(current, size);
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getFollower_user_id, userId)
               .orderByDesc(Follows::getCreated_at);

        Page<Follows> followPage = this.page(page, wrapper);

        Page<UserInfoVO> voPage = new Page<>(current, size);
        voPage.setTotal(followPage.getTotal());
        voPage.setRecords(followPage.getRecords().stream()
            .map(follow -> {
                Users user = usersService.getById(follow.getTarget_id());
                return convertToUserInfoVO(user);
            })
            .filter(vo -> vo != null)
            .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Page<UserInfoVO> getFollowerList(Long userId, Long current, Long size) {
        Page<Follows> page = new Page<>(current, size);
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getTarget_id, userId)
               .orderByDesc(Follows::getCreated_at);

        Page<Follows> followPage = this.page(page, wrapper);

        Page<UserInfoVO> voPage = new Page<>(current, size);
        voPage.setTotal(followPage.getTotal());
        voPage.setRecords(followPage.getRecords().stream()
            .map(follow -> {
                Users user = usersService.getById(follow.getFollower_user_id());
                return convertToUserInfoVO(user);
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
    public Long getFollowerCount(Long userId) {
        LambdaQueryWrapper<Follows> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Follows::getTarget_id, userId);
        return this.count(wrapper);
    }

    private UserInfoVO convertToUserInfoVO(Users user) {
        if (user == null || user.getDeleted() == 1) {
            return null;
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setDisplayName(user.getDisplay_name());
        vo.setAvatarUrl(user.getAvatar_url());
        vo.setBio(user.getBio());
        vo.setRole(user.getRole());
        return vo;
    }
}
