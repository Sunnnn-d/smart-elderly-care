package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_detail")
public class FeeDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long billId;

    private String billNo;

    private Long itemId;

    private String itemName;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}