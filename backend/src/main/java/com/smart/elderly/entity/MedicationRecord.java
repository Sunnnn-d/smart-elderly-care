package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("medication_record")
public class MedicationRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long planId;

    private Long elderlyId;

    private String elderlyName;

    private String medicineName;

    private String dosage;

    private LocalDateTime takeTime;

    private Integer status;

    private Long recorderId;

    private String recorderName;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}