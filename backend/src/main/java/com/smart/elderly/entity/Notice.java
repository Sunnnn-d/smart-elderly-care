package com.smart.elderly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告资讯实体
 */
@Data
@TableName("notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String coverImage;

    /** 类型：1-公告 2-健康资讯 3-活动通知 */
    private Integer type;

    /** 是否置顶：0-否 1-是 */
    private Integer isTop;

    private LocalDateTime publishTime;

    /** 状态：0-草稿 1-已发布 */
    private Integer status;

    private String author;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
