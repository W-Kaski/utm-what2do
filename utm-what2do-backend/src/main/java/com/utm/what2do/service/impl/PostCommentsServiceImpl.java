package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.PostComments;
import com.utm.what2do.service.PostCommentsService;
import com.utm.what2do.mapper.PostCommentsMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【post_comments】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Service
public class PostCommentsServiceImpl extends ServiceImpl<PostCommentsMapper, PostComments>
    implements PostCommentsService{

}




