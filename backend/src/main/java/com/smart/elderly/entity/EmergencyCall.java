package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("emergency_call")
public class EmergencyCall {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long elderlyId;

    private String elderlyName;

    private String roomNumber;

    private LocalDateTime callTime;

    private String callType;

    private Integer status;

    private Long responderId;

    private String responderName;

    private LocalDateTime responseTime;

    private String handleResult;

    private BigDecimal locationLat;

    private BigDecimal locationLng;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}