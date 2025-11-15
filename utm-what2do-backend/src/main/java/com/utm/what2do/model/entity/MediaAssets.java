package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName media_assets
 */
@TableName(value ="media_assets")
@Data
public class MediaAssets {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String hash;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private String mime_type;

    /**
     * 
     */
    private Long size_bytes;

    /**
     * 
     */
    private Integer width;

    /**
     * 
     */
    private Integer height;

    /**
     * 
     */
    private Date created_at;
}