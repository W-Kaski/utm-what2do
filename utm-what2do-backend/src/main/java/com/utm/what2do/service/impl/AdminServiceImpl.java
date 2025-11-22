package com.utm.what2do.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.mapper.ClubMembersMapper;
import com.utm.what2do.mapper.ClubsMapper;
import com.utm.what2do.mapper.EventsMapper;
import com.utm.what2do.mapper.PostCommentsMapper;
import com.utm.what2do.mapper.PostsMapper;
import com.utm.what2do.mapper.UsersMapper;
import com.utm.what2do.model.entity.*;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 管理员模块Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UsersMapper usersMapper;
    private final ClubsMapper clubsMapper;
    private final ClubMembersMapper clubMembersMapper;
    private final EventsMapper eventsMapper;
    private final PostsMapper postsMapper;
    private final PostCommentsMapper postCommentsMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    // 合法角色
    private static final Set<String> VALID_ROLES = Set.of(
            RoleConstants.USER, RoleConstants.CLUB_MANAGER, RoleConstants.ADMIN
    );

    // ==================== 用户与社团关系管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(Long userId, String role, Long adminId) {
        // 1. 校验目标角色合法性
        if (!VALID_ROLES.contains(role)) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                "角色无效，只能是 USER, CLUB_MANAGER 或 ADMIN");
        }

        // 2. 校验目标用户存在
        Users targetUser = usersMapper.selectById(userId);
        if (targetUser == null || targetUser.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 3. 自我降级保护
        if (userId.equals(adminId) && !RoleConstants.ADMIN.equals(role)) {
            // 检查是否是唯一的管理员
            LambdaQueryWrapper<Users> adminQuery = new LambdaQueryWrapper<>();
            adminQuery.eq(Users::getRole, RoleConstants.ADMIN)
                     .eq(Users::getDeleted, 0);
            Long adminCount = usersMapper.selectCount(adminQuery);
            if (adminCount <= 1) {
                throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                    "无法降级，您是唯一的管理员");
            }
        }

        // 4. 更新角色
        targetUser.setRole(role);
        usersMapper.updateById(targetUser);

        // 5. 强制目标用户会话失效
        if (StpUtil.isLogin(userId)) {
            StpUtil.logout(userId);
        }

        log.info("管理员修改用户角色: adminId={}, targetUserId={}, newRole={}",
            adminId, userId, role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long userId, Integer deleted, Long adminId) {
        // 1. 校验状态值
        if (deleted != 0 && deleted != 1) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                "状态值无效，只能是 0(解封) 或 1(禁用)");
        }

        // 2. 校验目标用户存在
        Users targetUser = usersMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 3. 防止禁用自己
        if (userId.equals(adminId) && deleted == 1) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                "无法禁用自己");
        }

        // 4. 更新状态
        targetUser.setDeleted(deleted);
        usersMapper.updateById(targetUser);

        // 5. 如果禁用，强制会话失效
        if (deleted == 1 && StpUtil.isLogin(userId)) {
            StpUtil.logout(userId);
        }

        log.info("管理员修改用户状态: adminId={}, targetUserId={}, deleted={}",
            adminId, userId, deleted);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addClubManager(Long clubId, Long userId, Long adminId) {
        // 1. 校验社团存在
        Clubs club = clubsMapper.selectById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 2. 校验用户存在
        Users user = usersMapper.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 3. 检查是否已经是该社团成员
        LambdaQueryWrapper<ClubMembers> memberQuery = new LambdaQueryWrapper<>();
        memberQuery.eq(ClubMembers::getClub_id, clubId)
                   .eq(ClubMembers::getUser_id, userId)
                   .eq(ClubMembers::getDeleted, 0);
        ClubMembers existing = clubMembersMapper.selectOne(memberQuery);

        if (existing != null) {
            // 已是成员，更新为MANAGER
            existing.setRole("MANAGER");
            clubMembersMapper.updateById(existing);
        } else {
            // 新增为MANAGER
            ClubMembers newMember = new ClubMembers();
            newMember.setClub_id(clubId);
            newMember.setUser_id(userId);
            newMember.setRole("MANAGER");
            newMember.setStatus("APPROVED");
            newMember.setJoined_at(new Date());
            newMember.setCreated_at(new Date());
            newMember.setDeleted(0);
            clubMembersMapper.insert(newMember);
        }

        // 4. 更新用户角色为CLUB_MANAGER（如果不是ADMIN）
        if (!RoleConstants.ADMIN.equals(user.getRole().toString())) {
            user.setRole(RoleConstants.CLUB_MANAGER);
            usersMapper.updateById(user);
        }

        log.info("管理员添加社团经理: adminId={}, clubId={}, userId={}",
            adminId, clubId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeClubManager(Long clubId, Long userId, Long adminId) {
        // 1. 校验社团存在
        Clubs club = clubsMapper.selectById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 2. 查找成员关系
        LambdaQueryWrapper<ClubMembers> memberQuery = new LambdaQueryWrapper<>();
        memberQuery.eq(ClubMembers::getClub_id, clubId)
                   .eq(ClubMembers::getUser_id, userId)
                   .eq(ClubMembers::getDeleted, 0);
        ClubMembers member = clubMembersMapper.selectOne(memberQuery);

        if (member == null) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(),
                "该用户不是社团成员");
        }

        // 3. 将角色降级为MEMBER
        member.setRole("MEMBER");
        clubMembersMapper.updateById(member);

        log.info("管理员移除社团经理: adminId={}, clubId={}, userId={}",
            adminId, clubId, userId);
    }

    @Override
    public Page<UserInfoVO> getUserList(Long current, Long size, String keyword) {
        Page<Users> page = new Page<>(current, size);

        LambdaQueryWrapper<Users> query = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            query.and(q -> q
                .like(Users::getUsername, keyword)
                .or()
                .like(Users::getEmail, keyword)
                .or()
                .like(Users::getDisplay_name, keyword)
            );
        }
        query.orderByDesc(Users::getCreated_at);

        Page<Users> usersPage = usersMapper.selectPage(page, query);

        // 转换为VO
        Page<UserInfoVO> voPage = new Page<>(usersPage.getCurrent(), usersPage.getSize(), usersPage.getTotal());
        List<UserInfoVO> voList = new ArrayList<>();
        for (Users user : usersPage.getRecords()) {
            voList.add(convertToUserInfoVO(user));
        }
        voPage.setRecords(voList);

        return voPage;
    }

    // ==================== 活动与地点数据管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markEventOfficial(Long eventId, Boolean isOfficial, Long adminId) {
        // 1. 校验活动存在
        Events event = eventsMapper.selectById(eventId);
        if (event == null || event.getDeleted() == 1) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 2. 更新官方标记
        event.setIs_official(isOfficial ? 1 : 0);
        eventsMapper.updateById(event);

        // 3. 清除缓存
        clearEventCache(eventId);

        log.info("管理员标记活动官方状态: adminId={}, eventId={}, isOfficial={}",
            adminId, eventId, isOfficial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEvent(Long eventId, Long adminId) {
        // 1. 校验活动存在
        Events event = eventsMapper.selectById(eventId);
        if (event == null) {
            throw new BusinessException(StatusCode.EVENT_NOT_FOUND);
        }

        // 2. 软删除活动
        event.setDeleted(1);
        eventsMapper.updateById(event);

        // 3. 更新社团活动计数
        if (event.getClub_id() != null) {
            Clubs club = clubsMapper.selectById(event.getClub_id());
            if (club != null && club.getEvents_count() > 0) {
                club.setEvents_count(club.getEvents_count() - 1);
                clubsMapper.updateById(club);
            }
        }

        // 4. 清除缓存
        clearEventCache(eventId);

        log.info("管理员删除活动: adminId={}, eventId={}", adminId, eventId);
    }

    // ==================== 内容审核与合规 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long adminId) {
        // 1. 校验帖子存在
        Posts post = postsMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(StatusCode.POST_NOT_FOUND);
        }

        // 2. 软删除帖子
        post.setDeleted(1);
        postsMapper.updateById(post);

        // 3. 级联软删除评论
        LambdaUpdateWrapper<PostComments> commentUpdate = new LambdaUpdateWrapper<>();
        commentUpdate.eq(PostComments::getPost_id, postId)
                     .set(PostComments::getDeleted, 1);
        postCommentsMapper.update(null, commentUpdate);

        // 4. 清除缓存
        clearPostCache(postId);

        log.info("管理员删除帖子: adminId={}, postId={}", adminId, postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long adminId) {
        // 1. 校验评论存在
        PostComments comment = postCommentsMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "评论不存在");
        }

        // 2. 软删除评论
        comment.setDeleted(1);
        postCommentsMapper.updateById(comment);

        // 3. 更新帖子评论计数
        LambdaUpdateWrapper<Posts> postUpdate = new LambdaUpdateWrapper<>();
        postUpdate.eq(Posts::getId, comment.getPost_id())
                  .setSql("comments_count = comments_count - 1");
        postsMapper.update(null, postUpdate);

        // 4. 清除帖子缓存
        clearPostCache(comment.getPost_id());

        log.info("管理员删除评论: adminId={}, commentId={}", adminId, commentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClub(Long clubId, Long adminId) {
        // 1. 校验社团存在
        Clubs club = clubsMapper.selectById(clubId);
        if (club == null) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 2. 软删除社团
        club.setDeleted(1);
        clubsMapper.updateById(club);

        // 3. 级联软删除 - 活动
        LambdaUpdateWrapper<Events> eventUpdate = new LambdaUpdateWrapper<>();
        eventUpdate.eq(Events::getClub_id, clubId)
                   .set(Events::getDeleted, 1);
        eventsMapper.update(null, eventUpdate);

        // 4. 级联软删除 - 成员关系
        LambdaUpdateWrapper<ClubMembers> memberUpdate = new LambdaUpdateWrapper<>();
        memberUpdate.eq(ClubMembers::getClub_id, clubId)
                    .set(ClubMembers::getDeleted, 1);
        clubMembersMapper.update(null, memberUpdate);

        // 5. 清除缓存
        clearClubCache(clubId);

        log.info("管理员删除社团: adminId={}, clubId={}", adminId, clubId);
    }

    // ==================== 系统维护与监控 ====================

    @Override
    public void clearCache(String cacheType) {
        try {
            if (cacheType == null || cacheType.isBlank() || "all".equals(cacheType)) {
                // 清除所有缓存
                Set<String> keys = redisTemplate.keys("*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
                log.info("清除所有Redis缓存");
            } else {
                // 清除特定类型缓存
                Set<String> keys = redisTemplate.keys(cacheType + ":*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
                log.info("清除Redis缓存: type={}", cacheType);
            }
        } catch (Exception e) {
            log.error("清除缓存失败: {}", e.getMessage());
            throw new BusinessException(500, "清除缓存失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        // 数据库状态
        try {
            usersMapper.selectCount(null);
            health.put("database", "healthy");
        } catch (Exception e) {
            health.put("database", "unhealthy: " + e.getMessage());
        }

        // Redis状态
        try {
            redisTemplate.opsForValue().get("health-check");
            health.put("redis", "healthy");
        } catch (Exception e) {
            health.put("redis", "unhealthy: " + e.getMessage());
        }

        // 内存使用
        Runtime runtime = Runtime.getRuntime();
        health.put("memory", Map.of(
            "total", runtime.totalMemory() / 1024 / 1024 + "MB",
            "free", runtime.freeMemory() / 1024 / 1024 + "MB",
            "used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "MB"
        ));

        // 时间戳
        health.put("timestamp", new Date());

        return health;
    }

    // ==================== 私有辅助方法 ====================

    private UserInfoVO convertToUserInfoVO(Users user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setDisplayName(user.getDisplay_name());
        vo.setAvatar(user.getAvatar_url());
        vo.setBio(user.getBio());
        vo.setRole(user.getRole() != null ? user.getRole().toString() : null);
        vo.setFollowingCount(user.getFollowing_count());
        vo.setFavoritesCount(user.getFavorites_count());

        if (user.getCreated_at() != null) {
            vo.setCreatedAt(LocalDateTime.ofInstant(
                user.getCreated_at().toInstant(),
                ZoneId.systemDefault()
            ));
        }

        return vo;
    }

    private void clearEventCache(Long eventId) {
        try {
            redisTemplate.delete("event:" + eventId);
            Set<String> listKeys = redisTemplate.keys("events:list:*");
            if (listKeys != null && !listKeys.isEmpty()) {
                redisTemplate.delete(listKeys);
            }
        } catch (Exception e) {
            log.warn("清除活动缓存失败: {}", e.getMessage());
        }
    }

    private void clearPostCache(Long postId) {
        try {
            redisTemplate.delete("post:" + postId);
            Set<String> listKeys = redisTemplate.keys("posts:list:*");
            if (listKeys != null && !listKeys.isEmpty()) {
                redisTemplate.delete(listKeys);
            }
        } catch (Exception e) {
            log.warn("清除帖子缓存失败: {}", e.getMessage());
        }
    }

    private void clearClubCache(Long clubId) {
        try {
            redisTemplate.delete("club:" + clubId);
            Set<String> listKeys = redisTemplate.keys("clubs:list:*");
            if (listKeys != null && !listKeys.isEmpty()) {
                redisTemplate.delete(listKeys);
            }
        } catch (Exception e) {
            log.warn("清除社团缓存失败: {}", e.getMessage());
        }
    }
}
