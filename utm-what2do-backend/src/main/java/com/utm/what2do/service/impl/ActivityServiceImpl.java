package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.mapper.ActivityMapper;
import com.utm.what2do.model.dto.ActivityQueryDTO;
import com.utm.what2do.model.dto.ActivityRequestDTO;
import com.utm.what2do.model.entity.Activity;
import com.utm.what2do.model.vo.ActivityVO;
import com.utm.what2do.service.ActivityService;
import com.utm.what2do.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public IPage<ActivityVO> pageActivities(ActivityQueryDTO queryDTO) {
        Page<ActivityVO> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        return this.baseMapper.selectActivities(page, queryDTO);
    }

    @Override
    public Activity createActivity(ActivityRequestDTO dto) {
        Activity activity = BeanCopyUtils.copy(dto, new Activity());
        this.save(activity);
        return activity;
    }

    @Override
    public Activity updateActivity(Long id, ActivityRequestDTO dto) {
        Activity activity = this.getById(id);
        if (activity == null) {
            throw new IllegalArgumentException("Activity not found: " + id);
        }
        BeanCopyUtils.copy(dto, activity);
        this.updateById(activity);
        return activity;
    }
}
