package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName posts
 */
@TableName(value ="posts")
@Data
public class Posts {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long author_user_id;

    /**
     * 
     */
    private Long author_club_id;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Integer is_pinned;

    /**
     * 
     */
    private Integer likes_count;

    /**
     * 
     */
    private Integer comments_count;

    /**
     * 
     */
    private Integer reposts_count;

    /**
     * 
     */
    private Date created_at;

    /**
     * 
     */
    private Date updated_at;

    /**
     * 
     */
    private Integer deleted;
}