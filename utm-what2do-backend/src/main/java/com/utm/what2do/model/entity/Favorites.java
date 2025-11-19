package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 收藏实体
 */
@TableName(value = "favorites")
@Data
public class Favorites {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long user_id;

    /**
     * 收藏类型：POST, EVENT
     */
    private String target_type;

    /**
     * 目标ID（帖子ID或活动ID）
     */
    private Long target_id;

    /**
     * 创建时间
     */
    private Date created_at;
}
