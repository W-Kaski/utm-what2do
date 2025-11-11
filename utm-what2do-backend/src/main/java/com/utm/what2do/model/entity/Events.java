package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName events
 */
@TableName(value ="events")
@Data
public class Events {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String slug;

    /**
     * 
     */
    private String description_long;

    /**
     * 
     */
    private Date start_date;

    /**
     * 
     */
    private Date start_time;

    /**
     * 
     */
    private Date end_time;

    /**
     * 
     */
    private String building_id;

    /**
     * 
     */
    private String room;

    /**
     * 
     */
    private Long club_id;

    /**
     * 
     */
    private String cover_url;

    /**
     * 
     */
    private Integer is_official;

    /**
     * 
     */
    private String registration_notes;

    /**
     * 
     */
    private Integer views_count;

    /**
     * 
     */
    private Integer likes_count;

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