package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 护理计划实体
 */
@Data
@TableName("care_plan")
public class CarePlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long elderlyId;

    private String elderlyName;

    private Long nurseId;

    private String nurseName;

    private String planName;

    /** 护理类型：日常护理/专项护理/康复护理 */
    private String careType;

    private String content;

    /** 执行频率：每天/每周/每月 */
    private String frequency;

    private LocalDate startDate;

    private LocalDate endDate;

    /** 状态：0-已停止 1-执行中 2-已完成 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
