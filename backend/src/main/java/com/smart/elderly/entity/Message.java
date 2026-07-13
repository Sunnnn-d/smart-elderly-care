package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("tb_message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收用户ID（客户端用户ID，为0表示系统消息）
     */
    private Long userId;

    /**
     * 消息接收者类型：user-客户端用户，admin-管理员
     */
    private String targetType;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 消息类型：system-系统通知，order-订单消息，service-服务提醒，other-其他
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer readFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}