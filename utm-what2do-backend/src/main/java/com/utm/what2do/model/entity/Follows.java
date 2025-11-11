package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName follows
 */
@TableName(value ="follows")
@Data
public class Follows {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long follower_user_id;

    /**
     * 
     */
    private Long target_id;

    /**
     * 
     */
    private Date created_at;
}