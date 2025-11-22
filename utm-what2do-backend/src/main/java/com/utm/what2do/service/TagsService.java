package com.utm.what2do.service;

import com.utm.what2do.model.entity.Tags;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.TagVO;

import java.util.List;

/**
* @author PC
* @description 针对表【tags】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface TagsService extends IService<Tags> {

    /**
     * 获取活动标签列表（使用Caffeine缓存）
     * @return 标签VO列表
     */
    List<TagVO> getEventTags();

    /**
     * 根据名称查找或创建标签
     * @param name 标签名称
     * @return 标签实体
     */
    Tags getOrCreateTag(String name);

    /**
     * 根据ID列表获取标签
     * @param ids 标签ID列表
     * @return 标签列表
     */
    List<Tags> getTagsByIds(List<Long> ids);
}
