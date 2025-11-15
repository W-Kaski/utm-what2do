package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName event_tags
 */
@TableName(value ="event_tags")
@Data
public class EventTags {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long event_id;

    /**
     * 
     */
    private Long tag_id;

    /**
     * 
     */
    private Date created_at;
}