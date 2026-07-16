package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String payNo;

    private Long billId;

    private String billNo;

    private Long elderlyId;

    private String elderlyName;

    private BigDecimal payAmount;

    private String payMethod;

    private LocalDateTime payTime;

    private Integer status;

    private String transactionId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}