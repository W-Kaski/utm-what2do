package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName post_comments
 */
@TableName(value ="post_comments")
@Data
public class PostComments {
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
    private Long user_id;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private Long parent_comment_id;

    /**
     * 
     */
    private Integer likes_count;

    /**
     * 
     */
    private Integer reviewed;

    /**
     * 
     */
    private Date created_at;

    /**
     * 
     */
    private Integer deleted;
}