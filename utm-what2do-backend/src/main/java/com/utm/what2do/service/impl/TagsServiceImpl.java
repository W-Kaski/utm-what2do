package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.Tags;
import com.utm.what2do.service.TagsService;
import com.utm.what2do.mapper.TagsMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【tags】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>
    implements TagsService{

}




