package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String activityName;

    private String activityType;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String coverImage;

    private Integer status;

    private Long organizerId;

    private String organizerName;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}