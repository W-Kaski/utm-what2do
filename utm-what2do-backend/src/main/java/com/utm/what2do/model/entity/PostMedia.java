package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName post_media
 */
@TableName(value ="post_media")
@Data
public class PostMedia {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long post_id;

    /**
     * 
     */
    private Object type;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private Integer order_index;

    /**
     * 
     */
    private Date created_at;
}