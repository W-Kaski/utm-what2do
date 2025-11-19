package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 活动收藏实体
 */
@TableName(value = "event_favorites")
@Data
public class EventFavorites {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long user_id;

    private Long event_id;

    private Date created_at;
}
