package com.utm.what2do.service;

import com.utm.what2do.model.dto.UserLoginDTO;
import com.utm.what2do.model.dto.UserRegisterDTO;
import com.utm.what2do.model.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.UserInfoVO;

import java.util.Map;

/**
* @author PC
* @description 针对表【users】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface UsersService extends IService<Users> {

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 用户信息
     */
    UserInfoVO register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息
     * @return Token和用户信息
     */
    Map<String, Object> login(UserLoginDTO dto);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户档案
     * @param userId 用户ID
     * @param dto 更新信息
     * @return 更新后的用户信息
     */
    UserInfoVO updateProfile(Long userId, UserInfoVO dto);
}
