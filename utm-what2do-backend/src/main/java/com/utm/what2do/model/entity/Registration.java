package com.utm.what2do.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.utm.what2do.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("registration")
public class Registration extends BaseEntity {

    @TableField("activity_id")
    private Long activityId;

    @TableField("user_id")
    private Long userId;

    private String status;

    @TableField("registered_at")
    private LocalDateTime registeredAt;

    private String remarks;
}
