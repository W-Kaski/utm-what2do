package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName clubs
 */
@TableName(value ="clubs")
@Data
public class Clubs {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String slug;

    /**
     * 
     */
    private String tagline;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Integer category;

    /**
     * 
     */
    private String logo_url;

    /**
     * 
     */
    private String cover_url;

    /**
     * 
     */
    private Integer members_count;

    /**
     * 
     */
    private Integer events_count;

    /**
     * 
     */
    private Integer posts_count;

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