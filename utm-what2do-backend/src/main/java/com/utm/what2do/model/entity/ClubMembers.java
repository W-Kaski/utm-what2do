package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName club_members
 */
@TableName(value ="club_members")
@Data
public class ClubMembers {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long club_id;

    /**
     * 
     */
    private Long user_id;

    /**
     * 
     */
    private Object role;

    /**
     * 
     */
    private Object status;

    /**
     * 
     */
    private Date joined_at;

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