package com.utm.what2do.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.dto.ActivityQueryDTO;
import com.utm.what2do.model.dto.ActivityRequestDTO;
import com.utm.what2do.model.entity.Activity;
import com.utm.what2do.model.vo.ActivityVO;

public interface ActivityService extends IService<Activity> {

    IPage<ActivityVO> pageActivities(ActivityQueryDTO queryDTO);

    Activity createActivity(ActivityRequestDTO dto);

    Activity updateActivity(Long id, ActivityRequestDTO dto);
}
