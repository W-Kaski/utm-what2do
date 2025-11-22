package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.model.entity.Tags;
import com.utm.what2do.model.vo.TagVO;
import com.utm.what2do.service.TagsService;
import com.utm.what2do.mapper.TagsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author PC
* @description 针对表【tags】的数据库操作Service实现
* @createDate 2025-11-11 02:14:33
*/
@Slf4j
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>
    implements TagsService{

    @Override
    @Cacheable(value = "tags", key = "'event_tags'")
    public List<TagVO> getEventTags() {
        LambdaQueryWrapper<Tags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tags::getDeleted, 0);
        wrapper.orderByAsc(Tags::getName);

        List<Tags> tags = this.list(wrapper);
        log.info("查询活动标签: count={}", tags.size());

        return tags.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tags getOrCreateTag(String name) {
        // 1. 查找现有标签
        LambdaQueryWrapper<Tags> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tags::getName, name);
        wrapper.eq(Tags::getDeleted, 0);
        Tags existing = this.getOne(wrapper);

        if (existing != null) {
            return existing;
        }

        // 2. 创建新标签
        Tags newTag = new Tags();
        newTag.setName(name);
        newTag.setCreated_at(new Date());
        newTag.setDeleted(0);

        this.save(newTag);
        log.info("创建新标签: id={}, name={}", newTag.getId(), name);

        return newTag;
    }

    @Override
    public List<Tags> getTagsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return this.listByIds(ids);
    }

    private TagVO convertToVO(Tags tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        return vo;
    }
}
