package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.Posts;
import com.utm.what2do.service.PostsService;
import com.utm.what2do.mapper.PostsMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【posts】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts>
    implements PostsService{

}




