package com.utm.what2do.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.common.response.StatusCode;
import com.utm.what2do.model.dto.UserLoginDTO;
import com.utm.what2do.model.dto.UserRegisterDTO;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.UsersService;
import com.utm.what2do.mapper.UsersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @author PC
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(UserRegisterDTO dto) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<Users> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(Users::getUsername, dto.getUsername());
        if (this.count(usernameQuery) > 0) {
            throw new BusinessException(StatusCode.USER_ALREADY_EXISTS);
        }

        // 2. 检查邮箱是否已存在
        LambdaQueryWrapper<Users> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(Users::getEmail, dto.getEmail());
        if (this.count(emailQuery) > 0) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        // 3. 创建用户实体
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setDisplay_name(dto.getDisplayName() != null ? dto.getDisplayName() : dto.getUsername());

        // 使用BCrypt加密密码
        String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        user.setPassword_hash(hashedPassword);

        user.setRole(dto.getRole());
        user.setAvatar_url(dto.getAvatar());
        user.setBio(dto.getBio());
        user.setFollowing_count(0);
        user.setFavorites_count(0);
        user.setCreated_at(new Date());
        user.setDeleted(0);

        // 4. 保存到数据库
        boolean saved = this.save(user);
        if (!saved) {
            throw new BusinessException(500, "注册失败");
        }

        // 5. 自动登录：使用Sa-Token
        StpUtil.login(user.getId());
        StpUtil.getSession().set("role", dto.getRole());

        log.info("用户注册成功: userId={}, username={}, role={}", user.getId(), user.getUsername(), dto.getRole());

        // 6. 返回用户信息
        return convertToVO(user);
    }

    @Override
    public Map<String, Object> login(UserLoginDTO dto) {
        // 1. 根据邮箱查询用户
        LambdaQueryWrapper<Users> query = new LambdaQueryWrapper<>();
        query.eq(Users::getEmail, dto.getEmail());
        Users user = this.getOne(query);

        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 2. 验证密码
        boolean passwordMatch = BCrypt.checkpw(dto.getPassword(), user.getPassword_hash());
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
        result.put("userInfo", convertToVO(user));
        return result;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        Users user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(Long userId, UserInfoVO dto) {
        Users user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.USER_NOT_FOUND);
        }

        // 更新允许修改的字段
        if (dto.getDisplayName() != null) {
            user.setDisplay_name(dto.getDisplayName());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar_url(dto.getAvatar());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }

        boolean updated = this.updateById(user);
        if (!updated) {
            throw new BusinessException(500, "更新失败");
        }

        log.info("用户档案更新成功: userId={}", userId);

        return convertToVO(user);
    }

    /**
     * 将Users实体转换为UserInfoVO
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

        // 转换Date为LocalDateTime
        if (user.getCreated_at() != null) {
            vo.setCreatedAt(LocalDateTime.ofInstant(
                user.getCreated_at().toInstant(),
                ZoneId.systemDefault()
            ));
        }

        return vo;
    }
}
