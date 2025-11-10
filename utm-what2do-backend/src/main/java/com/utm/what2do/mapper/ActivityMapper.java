package com.utm.what2do.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.utm.what2do.model.dto.ActivityQueryDTO;
import com.utm.what2do.model.entity.Activity;
import com.utm.what2do.model.vo.ActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    IPage<ActivityVO> selectActivities(Page<ActivityVO> page, @Param("query") ActivityQueryDTO query);
}
