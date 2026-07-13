package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康监测记录实体
 */
@Data
@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long elderlyId;

    private String elderlyName;

    private Integer heartRate;

    private Integer bloodPressureHigh;

    private Integer bloodPressureLow;

    private BigDecimal bloodSugar;

    private BigDecimal temperature;

    private BigDecimal weight;

    private BigDecimal oxygenSaturation;

    private BigDecimal sleepHours;

    /** 情绪状态：1-良好 2-一般 3-低落 */
    private Integer mood;

    private String remark;

    private LocalDateTime recordTime;

    private Long recorderId;

    private String recorderName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
