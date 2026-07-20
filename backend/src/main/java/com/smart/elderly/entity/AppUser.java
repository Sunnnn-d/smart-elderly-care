package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户端用户实体
 */
@Data
@TableName("app_user")
public class AppUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String avatar;

    private String phone;

    private String email;

    /** 性别：0-女 1-男 */
    private Integer gender;

    private Integer age;

    private String address;

    private Long elderlyId;

    private String emergencyContact;

    private String emergencyPhone;

    private String healthStatus;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    private LocalDateTime lastLoginTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
