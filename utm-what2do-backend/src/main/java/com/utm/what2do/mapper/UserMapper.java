package com.utm.what2do.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.dto.UserQueryDTO;
import com.utm.what2do.model.entity.User;
import com.utm.what2do.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<UserVO> selectUsers(Page<UserVO> page, @Param("query") UserQueryDTO query);
}
