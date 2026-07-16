package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bed")
public class Bed {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private String roomNumber;

    private String bedNumber;

    private Long elderlyId;

    private String elderlyName;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}