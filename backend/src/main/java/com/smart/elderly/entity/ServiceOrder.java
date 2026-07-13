package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务订单实体
 */
@Data
@TableName("service_order")
public class ServiceOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户端用户ID
     */
    private Long userId;

    private String orderNo;

    private Long elderlyId;

    private String elderlyName;

    private Long serviceId;

    private String serviceName;

    private BigDecimal servicePrice;

    private Long nurseId;

    private String nurseName;

    private LocalDateTime appointmentTime;

    private String contactPhone;

    private String address;

    private String remark;

    /** 订单状态：0-待派单 1-服务中 2-已完成 3-已取消 */
    private Integer status;

    private LocalDateTime completeTime;

    private String cancelReason;

    /** 取消类型：manual-用户自主取消，admin-管理员取消，timeout-超时自动取消 */
    private String cancelType;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /** 评价分数（非数据库字段） */
    @TableField(exist = false)
    private Integer evalScore;

    /** 评价内容（非数据库字段） */
    @TableField(exist = false)
    private String evalContent;
}
