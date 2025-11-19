package com.utm.what2do.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.utm.what2do.model.entity.EventFavorites;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动收藏Mapper
 */
@Mapper
public interface EventFavoritesMapper extends BaseMapper<EventFavorites> {
}
