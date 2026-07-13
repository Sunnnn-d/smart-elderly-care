package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 老人档案实体
 */
@Data
@TableName("elderly_profile")
public class ElderlyProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 性别：0-女 1-男 */
    private Integer gender;

    private Integer age;

    private String idCard;

    private String phone;

    private String emergencyContact;

    private String emergencyPhone;

    private String address;

    private String bloodType;

    private String allergies;

    private String chronicDiseases;

    /** 护理等级：1-自理 2-半护理 3-全护理 */
    private Integer careLevel;

    private String roomNumber;

    private String bedNumber;

    private String avatar;

    private String remark;

    /** 状态：0-离院 1-在院 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
