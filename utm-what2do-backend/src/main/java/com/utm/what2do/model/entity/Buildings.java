package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName buildings
 */
@TableName(value ="buildings")
@Data
public class Buildings {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private BigDecimal lat;

    /**
     * 
     */
    private BigDecimal lng;

    /**
     * 
     */
    private String campus_zone;

    /**
     * 
     */
    private Integer category;

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