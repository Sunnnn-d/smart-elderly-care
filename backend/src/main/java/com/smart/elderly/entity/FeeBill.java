package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_bill")
public class FeeBill {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String billNo;

    private Long elderlyId;

    private String elderlyName;

    private String billMonth;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private Integer status;

    private LocalDate dueDate;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}