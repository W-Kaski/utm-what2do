package com.utm.what2do.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.utm.what2do.model.entity.Favorites;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏Mapper
 */
@Mapper
public interface FavoritesMapper extends BaseMapper<Favorites> {
}
