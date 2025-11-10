package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.mapper.UserMapper;
import com.utm.what2do.model.dto.UserQueryDTO;
import com.utm.what2do.model.dto.UserRequestDTO;
import com.utm.what2do.model.entity.User;
import com.utm.what2do.model.vo.UserVO;
import com.utm.what2do.service.UserService;
import com.utm.what2do.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public IPage<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<UserVO> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        return this.baseMapper.selectUsers(page, queryDTO);
    }

    @Override
    public User createUser(UserRequestDTO dto) {
        User user = BeanCopyUtils.copy(dto, new User());
        this.save(user);
        return user;
    }

    @Override
    public User updateUser(Long id, UserRequestDTO dto) {
        User user = this.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        BeanCopyUtils.copy(dto, user);
        this.updateById(user);
        return user;
    }
}
