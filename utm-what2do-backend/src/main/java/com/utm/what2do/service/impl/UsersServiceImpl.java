package com.utm.what2do.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.model.dto.UserLoginDTO;
import com.utm.what2do.model.dto.UserRegisterDTO;
import com.utm.what2do.model.entity.Clubs;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.ClubsService;
import com.utm.what2do.service.FollowsService;
import com.utm.what2do.service.UsersService;
import com.utm.what2do.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
* @author PC
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

    private final FollowsService followsService;
    private final ClubsService clubsService;

    // 使用@Lazy避免循环依赖
    public UsersServiceImpl(@Lazy FollowsService followsService, @Lazy ClubsService clubsService) {
        this.followsService = followsService;
        this.clubsService = clubsService;
    }

    // 用户名格式：只能包含字母、数字、下划线
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    // 合法角色
    private static final String ROLE_USER = "USER";
    private static final String ROLE_CLUB_MANAGER = "CLUB_MANAGER";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(UserRegisterDTO dto) {
        // 1. 验证用户名格式
        if (!USERNAME_PATTERN.matcher(dto.getUsername()).matches()) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "用户名只能包含字母、数字和下划线");
        }

        // 2. 验证密码复杂度（至少8位，包含字母和数字）
        String password = dto.getPassword();
        if (password.length() < 8) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "密码长度至少8位");
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "密码必须包含字母和数字");
        }

        // 3. 验证角色合法性
        String role = dto.getRole();
        if (role != null && !role.equals(ROLE_USER) && !role.equals(ROLE_CLUB_MANAGER)) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "角色无效，只能是 USER 或 CLUB_MANAGER");
        }

        // 4. 检查用户名是否已存在
        LambdaQueryWrapper<Users> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(Users::getUsername, dto.getUsername());
        if (this.count(usernameQuery) > 0) {
            throw new BusinessException(StatusCode.USER_ALREADY_EXISTS);
        }

        // 5. 检查邮箱是否已存在
        LambdaQueryWrapper<Users> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(Users::getEmail, dto.getEmail());
        if (this.count(emailQuery) > 0) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "邮箱已被注册");
        }

        // 6. 创建用户实体
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setDisplay_name(dto.getDisplayName() != null ? dto.getDisplayName() : dto.getUsername());

        // 使用BCrypt加密密码
        String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        user.setPassword_hash(hashedPassword);

        // 设置默认值
        user.setRole(role != null ? role : ROLE_USER);
        user.setAvatar_url(dto.getAvatar());
        user.setBio(dto.getBio());
        user.setFollowing_count(0);
        user.setFavorites_count(0);
        user.setCreated_at(new Date());
        user.setDeleted(0);

        // 7. 保存到数据库
        boolean saved = this.save(user);
        if (!saved) {
            throw new BusinessException(500, "注册失败");
        }

        // 8. 自动登录：使用Sa-Token
        StpUtil.login(user.getId());
        StpUtil.getSession().set("role", user.getRole());

        log.info("用户注册成功: userId={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());

        // 9. 返回用户信息
        return convertToVO(user);
    }

    @Override
    public Map<String, Object> login(UserLoginDTO dto) {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<Users> query = new LambdaQueryWrapper<>();
        query.eq(Users::getUsername, dto.getUsername());
        Users user = this.getOne(query);

        log.info("登录尝试: username={}, userFound={}", dto.getUsername(), user != null);

        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 2. 验证密码
        boolean passwordMatch = BCrypt.checkpw(dto.getPassword(), user.getPassword_hash());
        log.info("密码验证: username={}, match={}", dto.getUsername(), passwordMatch);

        if (!passwordMatch) {
            throw new BusinessException(StatusCode.PASSWORD_ERROR);
        }

        // 3. 使用Sa-Token登录
        StpUtil.login(user.getId());
        StpUtil.getSession().set("role", user.getRole());

        // 4. 更新最后登录时间
        user.setLast_login_at(new Date());
        this.updateById(user);

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        // 5. 返回Token和用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("user", convertToVO(user));
        return result;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        Users user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    public UserInfoVO getPublicProfile(Long userId) {
        Users user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }
        // 返回公开信息，不包含email等敏感字段
        return convertToPublicVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(Long userId, UserInfoVO dto) {
        Users user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 验证并更新displayName
        if (dto.getDisplayName() != null) {
            String displayName = dto.getDisplayName().trim();
            if (displayName.length() < 2 || displayName.length() > 120) {
                throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "显示名称长度必须在2-120字符之间");
            }
            user.setDisplay_name(displayName);
        }

        // 验证并更新bio
        if (dto.getBio() != null) {
            if (dto.getBio().length() > 512) {
                throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "个人简介不能超过512字符");
            }
            user.setBio(dto.getBio());
        }

        // 更新头像
        if (dto.getAvatar() != null) {
            user.setAvatar_url(dto.getAvatar());
        }

        boolean updated = this.updateById(user);
        if (!updated) {
            throw new BusinessException(500, "更新失败");
        }

        log.info("用户档案更新成功: userId={}", userId);

        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followClub(Long userId, Long clubId) {
        // 1. 验证社团存在
        Clubs club = clubsService.getById(clubId);
        if (club == null || club.getDeleted() == 1) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 2. 检查是否已关注
        if (followsService.isFollowingClub(userId, clubId)) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "已关注该社团");
        }

        // 3. 创建关注关系（FollowsService会更新计数）
        followsService.followClub(userId, clubId);

        log.info("用户关注社团成功: userId={}, clubId={}", userId, clubId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowClub(Long userId, Long clubId) {
        // 1. 验证社团存在
        Clubs club = clubsService.getById(clubId);
        if (club == null) {
            throw new BusinessException(StatusCode.CLUB_NOT_FOUND);
        }

        // 2. 检查是否已关注
        if (!followsService.isFollowingClub(userId, clubId)) {
            throw new BusinessException(StatusCode.BAD_REQUEST.getCode(), "未关注该社团");
        }

        // 3. 删除关注关系（FollowsService会更新计数）
        followsService.unfollowClub(userId, clubId);

        log.info("用户取消关注社团成功: userId={}, clubId={}", userId, clubId);
    }

    /**
     * 将Users实体转换为UserInfoVO（完整信息）
     */
    private UserInfoVO convertToVO(Users user) {
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

        // 转换Date为LocalDateTime
        if (user.getCreated_at() != null) {
            vo.setCreatedAt(LocalDateTime.ofInstant(
                user.getCreated_at().toInstant(),
                ZoneId.systemDefault()
            ));
        }

        return vo;
    }

    /**
     * 将Users实体转换为公开的UserInfoVO（不包含敏感信息）
     */
    private UserInfoVO convertToPublicVO(Users user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        // 不返回email
        vo.setDisplayName(user.getDisplay_name());
        vo.setAvatar(user.getAvatar_url());
        vo.setBio(user.getBio());
        // 不返回role
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
}
