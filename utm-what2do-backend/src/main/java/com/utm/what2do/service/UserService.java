package com.utm.what2do.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.dto.UserQueryDTO;
import com.utm.what2do.model.dto.UserRequestDTO;
import com.utm.what2do.model.entity.User;
import com.utm.what2do.model.vo.UserVO;

public interface UserService extends IService<User> {

    IPage<UserVO> pageUsers(UserQueryDTO queryDTO);

    User createUser(UserRequestDTO dto);

    User updateUser(Long id, UserRequestDTO dto);
}
