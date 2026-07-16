package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_signup")
public class ActivitySignup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private String activityName;

    private Long elderlyId;

    private String elderlyName;

    private LocalDateTime signupTime;

    private Integer status;

    private LocalDateTime signInTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}