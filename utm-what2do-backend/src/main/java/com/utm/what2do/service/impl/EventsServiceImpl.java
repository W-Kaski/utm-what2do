package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.Events;
import com.utm.what2do.service.EventsService;
import com.utm.what2do.mapper.EventsMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【events】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Service
public class EventsServiceImpl extends ServiceImpl<EventsMapper, Events>
    implements EventsService{

}




