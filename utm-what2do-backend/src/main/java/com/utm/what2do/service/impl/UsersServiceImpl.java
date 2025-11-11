package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.service.UsersService;
import com.utm.what2do.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【users】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




