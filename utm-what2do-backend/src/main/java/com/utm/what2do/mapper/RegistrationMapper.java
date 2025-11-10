package com.utm.what2do.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.dto.RegistrationQueryDTO;
import com.utm.what2do.model.entity.Registration;
import com.utm.what2do.model.vo.RegistrationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    IPage<RegistrationVO> selectRegistrations(Page<RegistrationVO> page, @Param("query") RegistrationQueryDTO query);
}
