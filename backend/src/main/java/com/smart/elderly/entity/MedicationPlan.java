package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("medication_plan")
public class MedicationPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long elderlyId;

    private String elderlyName;

    private String medicineName;

    private String dosage;

    private String frequency;

    private Integer timesPerDay;

    private String takeTimes;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long doctorId;

    private String doctorName;

    private String prescriptionNo;

    private String remark;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}