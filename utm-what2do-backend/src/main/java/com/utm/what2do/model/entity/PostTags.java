package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName post_tags
 */
@TableName(value ="post_tags")
@Data
public class PostTags {
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
    private Long tag_id;

    /**
     * 
     */
    private Date created_at;
}