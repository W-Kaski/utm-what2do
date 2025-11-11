package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String display_name;

    /**
     * 
     */
    private String password_hash;

    /**
     * 
     */
    private Object role;

    /**
     * 
     */
    private String avatar_url;

    /**
     * 
     */
    private String cover_url;

    /**
     * 
     */
    private String bio;

    /**
     * 
     */
    private Integer following_count;

    /**
     * 
     */
    private Integer favorites_count;

    /**
     * 
     */
    private Date last_login_at;

    /**
     * 
     */
    private Date created_at;

    /**
     * 
     */
    private Integer deleted;
}