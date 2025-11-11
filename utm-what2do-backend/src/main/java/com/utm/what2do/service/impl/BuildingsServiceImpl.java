package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.Buildings;
import com.utm.what2do.service.BuildingsService;
import com.utm.what2do.mapper.BuildingsMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【buildings】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Service
public class BuildingsServiceImpl extends ServiceImpl<BuildingsMapper, Buildings>
    implements BuildingsService{

}




