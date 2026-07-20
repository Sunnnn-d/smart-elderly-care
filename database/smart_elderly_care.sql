-- ============================================================
-- 智慧养老系统 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `smart_elderly_care` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `smart_elderly_care`;

-- ============================================================
-- 1. 系统用户表（管理系统用户）
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `role` TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1-超级管理员 2-普通管理员 3-护理员 4-医生 5-营养师 6-康复师 7-保洁员',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ============================================================
-- 1.1 角色表
-- ============================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================
-- 1.2 客户端用户表（老人/家属/预约用户）
-- ============================================================
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名/账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` TINYINT DEFAULT 1 COMMENT '性别：0-女 1-男',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
  `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
  `health_status` VARCHAR(50) DEFAULT NULL COMMENT '健康状况',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户端用户表';

-- ============================================================
-- 2. 老人档案表
-- ============================================================
DROP TABLE IF EXISTS `elderly_profile`;
CREATE TABLE `elderly_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT NOT NULL DEFAULT 1 COMMENT '性别：0-女 1-男',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '居住地址',
  `blood_type` VARCHAR(10) DEFAULT NULL COMMENT '血型',
  `allergies` VARCHAR(500) DEFAULT NULL COMMENT '过敏史',
  `chronic_diseases` VARCHAR(500) DEFAULT NULL COMMENT '慢性病',
  `care_level` TINYINT NOT NULL DEFAULT 1 COMMENT '护理等级：1-自理 2-半护理 3-全护理',
  `room_number` VARCHAR(20) DEFAULT NULL COMMENT '房间号',
  `bed_number` VARCHAR(20) DEFAULT NULL COMMENT '床位号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-离院 1-在院',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人档案表';

-- ============================================================
-- 3. 服务项目表
-- ============================================================
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `category` VARCHAR(20) NOT NULL COMMENT '服务分类：助餐/保洁/护理/康复/陪伴/其他',
  `description` TEXT COMMENT '服务描述',
  `image` VARCHAR(255) DEFAULT NULL COMMENT '服务图片',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '价格',
  `unit` VARCHAR(20) DEFAULT '次' COMMENT '计价单位',
  `duration` INT DEFAULT 60 COMMENT '服务时长（分钟）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架 1-上架',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- ============================================================
-- 4. 服务订单表
-- ============================================================
DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '客户端用户ID',
  `order_no` VARCHAR(30) NOT NULL COMMENT '订单编号',
  `elderly_id` BIGINT DEFAULT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `service_id` BIGINT DEFAULT NULL COMMENT '服务项目ID',
  `service_name` VARCHAR(100) DEFAULT NULL COMMENT '服务名称',
  `service_price` DECIMAL(10,2) DEFAULT NULL COMMENT '服务价格',
  `nurse_id` BIGINT DEFAULT NULL COMMENT '护理员ID',
  `nurse_name` VARCHAR(50) DEFAULT NULL COMMENT '护理员姓名',
  `appointment_time` DATETIME DEFAULT NULL COMMENT '预约时间',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '服务地址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待派单 1-服务中 2-已完成 3-已取消',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `cancel_reason` VARCHAR(500) DEFAULT NULL COMMENT '取消原因',
  `cancel_type` VARCHAR(20) DEFAULT 'manual' COMMENT '取消类型：manual-用户自主取消，admin-管理员取消，timeout-超时自动取消',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cancel_type` (`cancel_type`),
  KEY `idx_cancel_time` (`cancel_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务订单表';

-- ============================================================
-- 5. 健康监测记录表
-- ============================================================
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `heart_rate` INT DEFAULT NULL COMMENT '心率（次/分）',
  `blood_pressure_high` INT DEFAULT NULL COMMENT '收缩压',
  `blood_pressure_low` INT DEFAULT NULL COMMENT '舒张压',
  `blood_sugar` DECIMAL(5,2) DEFAULT NULL COMMENT '血糖（mmol/L）',
  `temperature` DECIMAL(4,1) DEFAULT NULL COMMENT '体温（℃）',
  `weight` DECIMAL(5,1) DEFAULT NULL COMMENT '体重（kg）',
  `oxygen_saturation` DECIMAL(5,2) DEFAULT NULL COMMENT '血氧饱和度（%）',
  `sleep_hours` DECIMAL(4,1) DEFAULT NULL COMMENT '睡眠时长（小时）',
  `mood` TINYINT DEFAULT 1 COMMENT '情绪状态：1-良好 2-一般 3-低落',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `record_time` DATETIME NOT NULL COMMENT '记录时间',
  `recorder_id` BIGINT DEFAULT NULL COMMENT '记录人ID',
  `recorder_name` VARCHAR(50) DEFAULT NULL COMMENT '记录人姓名',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康监测记录表';

-- ============================================================
-- 6. 护理计划表
-- ============================================================
DROP TABLE IF EXISTS `care_plan`;
CREATE TABLE `care_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `nurse_id` BIGINT DEFAULT NULL COMMENT '护理员ID',
  `nurse_name` VARCHAR(50) DEFAULT NULL COMMENT '护理员姓名',
  `plan_name` VARCHAR(100) NOT NULL COMMENT '计划名称',
  `care_type` VARCHAR(20) NOT NULL COMMENT '护理类型：日常护理/专项护理/康复护理',
  `content` TEXT COMMENT '护理内容',
  `frequency` VARCHAR(20) DEFAULT '每天' COMMENT '执行频率：每天/每周/每月',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已停止 1-执行中 2-已完成',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理计划表';

-- ============================================================
-- 7. 评价表
-- ============================================================
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(30) DEFAULT NULL COMMENT '订单编号',
  `elderly_id` BIGINT DEFAULT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `nurse_id` BIGINT DEFAULT NULL COMMENT '护理员ID',
  `nurse_name` VARCHAR(50) DEFAULT NULL COMMENT '护理员姓名',
  `score` TINYINT NOT NULL DEFAULT 5 COMMENT '评分：1-5分',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ============================================================
-- 8. 公告资讯表
-- ============================================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-公告 2-健康资讯 3-活动通知',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已发布',
  `author` VARCHAR(50) DEFAULT NULL COMMENT '作者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告资讯表';

-- ============================================================
-- 9. 轮播图表
-- ============================================================
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image` VARCHAR(255) NOT NULL COMMENT '图片URL',
  `link_url` VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ============================================================
-- 10. 用药计划表（用药管理模块）
-- ============================================================
DROP TABLE IF EXISTS `medication_plan`;
CREATE TABLE `medication_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `medicine_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
  `dosage` VARCHAR(50) NOT NULL COMMENT '剂量',
  `frequency` VARCHAR(20) DEFAULT '每天' COMMENT '用药频率：每天/每周/每月',
  `times_per_day` INT DEFAULT 1 COMMENT '每天用药次数',
  `take_times` VARCHAR(100) DEFAULT NULL COMMENT '用药时间（如：08:00,12:00,18:00）',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '开医嘱医生ID',
  `doctor_name` VARCHAR(50) DEFAULT NULL COMMENT '医生姓名',
  `prescription_no` VARCHAR(50) DEFAULT NULL COMMENT '医嘱编号',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已停用 1-执行中',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药计划表';

-- ============================================================
-- 11. 用药记录表（用药管理模块）
-- ============================================================
DROP TABLE IF EXISTS `medication_record`;
CREATE TABLE `medication_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` BIGINT NOT NULL COMMENT '用药计划ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `medicine_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
  `dosage` VARCHAR(50) NOT NULL COMMENT '服用剂量',
  `take_time` DATETIME NOT NULL COMMENT '实际服用时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未服用 1-已服用 2-漏服 3-拒服',
  `recorder_id` BIGINT DEFAULT NULL COMMENT '记录人ID',
  `recorder_name` VARCHAR(50) DEFAULT NULL COMMENT '记录人姓名',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_plan_id` (`plan_id`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药记录表';

-- ============================================================
-- 12. 紧急呼叫表（紧急呼叫模块）
-- ============================================================
DROP TABLE IF EXISTS `emergency_call`;
CREATE TABLE `emergency_call` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `room_number` VARCHAR(20) DEFAULT NULL COMMENT '房间号',
  `call_time` DATETIME NOT NULL COMMENT '呼叫时间',
  `call_type` VARCHAR(20) DEFAULT 'emergency' COMMENT '呼叫类型：emergency-紧急求助 fall-跌倒报警 health-健康异常 other-其他',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待响应 1-处理中 2-已完成 3-已取消',
  `responder_id` BIGINT DEFAULT NULL COMMENT '响应人员ID',
  `responder_name` VARCHAR(50) DEFAULT NULL COMMENT '响应人员姓名',
  `response_time` DATETIME DEFAULT NULL COMMENT '响应时间',
  `handle_result` VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
  `location_lat` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
  `location_lng` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紧急呼叫表';

-- ============================================================
-- 13. 房间表（床位管理模块）
-- ============================================================
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
  `floor` INT DEFAULT 1 COMMENT '楼层',
  `room_type` VARCHAR(20) DEFAULT 'double' COMMENT '房间类型：single-单人间 double-双人间 triple-三人间 suite-套房',
  `bed_count` INT DEFAULT 2 COMMENT '床位数量',
  `window_direction` VARCHAR(20) DEFAULT NULL COMMENT '朝向：东/南/西/北/东南/西南',
  `facility` VARCHAR(500) DEFAULT NULL COMMENT '配套设施',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用 1-可用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_number` (`room_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- ============================================================
-- 14. 床位表（床位管理模块）
-- ============================================================
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` BIGINT NOT NULL COMMENT '房间ID',
  `room_number` VARCHAR(20) DEFAULT NULL COMMENT '房间号',
  `bed_number` VARCHAR(20) NOT NULL COMMENT '床位号',
  `elderly_id` BIGINT DEFAULT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `check_in_time` DATETIME DEFAULT NULL COMMENT '入住时间',
  `check_out_time` DATETIME DEFAULT NULL COMMENT '离院时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-空闲 1-已入住 2-维修中',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bed_number` (`room_id`, `bed_number`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位表';

-- ============================================================
-- 15. 费用项目表（费用管理模块）
-- ============================================================
DROP TABLE IF EXISTS `fee_item`;
CREATE TABLE `fee_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_name` VARCHAR(100) NOT NULL COMMENT '费用名称',
  `fee_type` VARCHAR(20) DEFAULT 'monthly' COMMENT '计费方式：monthly-按月 daily-按天 per_time-按次',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '单价',
  `unit` VARCHAR(20) DEFAULT '月' COMMENT '计价单位',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '费用描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用项目表';

-- ============================================================
-- 16. 费用账单表（费用管理模块）
-- ============================================================
DROP TABLE IF EXISTS `fee_bill`;
CREATE TABLE `fee_bill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_no` VARCHAR(30) NOT NULL COMMENT '账单编号',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `bill_month` VARCHAR(7) NOT NULL COMMENT '账单月份（格式：yyyy-MM）',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '总金额',
  `paid_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '已付金额',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-部分支付 2-已结清',
  `due_date` DATE DEFAULT NULL COMMENT '到期日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用账单表';

-- ============================================================
-- 17. 费用明细表（费用管理模块）
-- ============================================================
DROP TABLE IF EXISTS `fee_detail`;
CREATE TABLE `fee_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_id` BIGINT NOT NULL COMMENT '账单ID',
  `bill_no` VARCHAR(30) DEFAULT NULL COMMENT '账单编号',
  `item_id` BIGINT NOT NULL COMMENT '费用项目ID',
  `item_name` VARCHAR(100) DEFAULT NULL COMMENT '费用项目名称',
  `quantity` DECIMAL(10,2) DEFAULT 1 COMMENT '数量',
  `unit_price` DECIMAL(10,2) DEFAULT 0 COMMENT '单价',
  `amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '金额',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_bill_id` (`bill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用明细表';

-- ============================================================
-- 18. 支付记录表（支付模块）
-- ============================================================
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pay_no` VARCHAR(30) NOT NULL COMMENT '支付编号',
  `bill_id` BIGINT DEFAULT NULL COMMENT '账单ID',
  `bill_no` VARCHAR(30) DEFAULT NULL COMMENT '账单编号',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `pay_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '支付金额',
  `pay_method` VARCHAR(20) DEFAULT 'wechat' COMMENT '支付方式：wechat-微信支付 alipay-支付宝 bank-银行转账 cash-现金',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-支付中 2-支付成功 3-支付失败',
  `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ============================================================
-- 19. 活动表（活动管理模块）
-- ============================================================
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_name` VARCHAR(100) NOT NULL COMMENT '活动名称',
  `activity_type` VARCHAR(20) DEFAULT 'culture' COMMENT '活动类型：culture-文化娱乐 sports-体育健身 health-健康讲座 volunteer-志愿者活动 other-其他',
  `description` TEXT COMMENT '活动描述',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '活动地点',
  `max_participants` INT DEFAULT 20 COMMENT '最大参与人数',
  `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未发布 1-报名中 2-进行中 3-已结束',
  `organizer_id` BIGINT DEFAULT NULL COMMENT '组织者ID',
  `organizer_name` VARCHAR(50) DEFAULT NULL COMMENT '组织者姓名',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- ============================================================
-- 20. 活动报名表（活动管理模块）
-- ============================================================
DROP TABLE IF EXISTS `activity_signup`;
CREATE TABLE `activity_signup` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `activity_name` VARCHAR(100) DEFAULT NULL COMMENT '活动名称',
  `elderly_id` BIGINT NOT NULL COMMENT '老人ID',
  `elderly_name` VARCHAR(50) DEFAULT NULL COMMENT '老人姓名',
  `signup_time` DATETIME NOT NULL COMMENT '报名时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已签到 3-已取消',
  `sign_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_elderly` (`activity_id`, `elderly_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_elderly_id` (`elderly_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- ============================================================
-- 21. 消息通知表（消息通知模块）
-- ============================================================
DROP TABLE IF EXISTS `tb_message`;
CREATE TABLE `tb_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL DEFAULT 0 COMMENT '接收用户ID（客户端用户ID，为0表示系统消息）',
  `target_type` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '消息接收者类型：user-客户端用户，admin-管理员',
  `order_id` BIGINT DEFAULT 0 COMMENT '关联订单ID',
  `type` VARCHAR(20) NOT NULL DEFAULT 'other' COMMENT '消息类型：system-系统通知，order-订单消息，service-服务提醒，other-其他',
  `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `read_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_read_flag` (`read_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';


-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员账号（密码：123456，BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('admin', '$2a$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '系统管理员', '13800000000', 1, 1),
('manager', '$2a$10$YBVB5qTYI/izetFTSQMyM./HhmdGVPc6vrSiHpodT81A05lOL1ONO', '张管理', '13800000001', 2, 1),
('nurse1', '$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '张护理', '13800000002', 3, 1),
('nurse2', '$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '李护理', '13800000003', 3, 1);

-- 客户端用户测试账号（密码：a123456，BCrypt加密）
-- 每个老人对应一个监护人账号，监护人可以登录移动端查看老人信息
INSERT INTO `app_user` (`username`, `password`, `real_name`, `phone`, `gender`, `age`, `address`, `emergency_contact`, `emergency_phone`, `health_status`, `status`) VALUES
('user1', '$2b$12$njl10MfjRrzCIwS05RXFFeo8ECm2CtXfKCjokyx6/6o61Th.V2hBm', '王明', '13900000001', 1, 45, '幸福路100号3栋201', '王大爷', '13700000001', '良好', 1),
('user2', '$2b$12$njl10MfjRrzCIwS05RXFFeo8ECm2CtXfKCjokyx6/6o61Th.V2hBm', '李华', '13900000002', 0, 38, '和平街50号1单元302', '李奶奶', '13700000002', '一般', 1),
('user3', '$2b$12$njl10MfjRrzCIwS05RXFFeo8ECm2CtXfKCjokyx6/6o61Th.V2hBm', '张强', '13900000003', 1, 42, '建设路88号2栋101', '张爷爷', '13700000003', '良好', 1),
('user4', '$2b$12$njl10MfjRrzCIwS05RXFFeo8ECm2CtXfKCjokyx6/6o61Th.V2hBm', '赵伟', '13900000004', 1, 39, '人民路66号5栋402', '赵奶奶', '13700000004', '良好', 1),
('user5', '$2b$12$njl10MfjRrzCIwS05RXFFeo8ECm2CtXfKCjokyx6/6o61Th.V2hBm', '刘刚', '13900000005', 1, 40, '光明路22号3单元501', '刘大爷', '13700000005', '良好', 1);

-- 老人档案
INSERT INTO `elderly_profile` (`name`, `gender`, `age`, `phone`, `emergency_contact`, `emergency_phone`, `address`, `blood_type`, `allergies`, `chronic_diseases`, `care_level`, `room_number`, `bed_number`, `status`, `remark`) VALUES
('王大爷', 1, 78, '13900000001', '王明', '13700000001', '幸福路100号3栋201', 'A', '青霉素', '高血压、糖尿病', 2, '101', '1', 1, '需要定期监测血压'),
('李奶奶', 0, 82, '13900000002', '李华', '13700000002', '和平街50号1单元302', 'O', '无', '骨质疏松', 3, '101', '2', 1, '行动不便，需全护理'),
('张爷爷', 1, 75, '13900000003', '张强', '13700000003', '建设路88号2栋101', 'B', '磺胺类', '冠心病', 2, '102', '1', 1, '膝关节术后康复中'),
('赵奶奶', 0, 80, '13900000004', '赵伟', '13700000004', '人民路66号5栋402', 'AB', '无', '轻度认知障碍', 1, '102', '2', 1, '需要陪伴聊天'),
('刘大爷', 1, 70, '13900000005', '刘刚', '13700000005', '光明路22号3单元501', 'A', '无', '高血压', 1, '103', '1', 1, NULL);

-- 服务项目
INSERT INTO `service_item` (`name`, `category`, `description`, `price`, `unit`, `duration`, `status`, `sort_order`) VALUES
('营养助餐', '助餐', '由专业营养师搭配，提供低盐低油、营养均衡的老年餐，支持堂食和送餐上门', 25.00, '次', 60, 1, 1),
('居家保洁', '保洁', '专业保洁人员上门服务，包含地面清洁、厨卫消毒、衣物整理等', 80.00, '次', 120, 1, 2),
('日常护理', '护理', '包含协助如厕、更换衣物、测量体征、用药提醒等日常护理服务', 120.00, '次', 180, 1, 3),
('康复理疗', '康复', '持证康复师提供关节活动、肌肉训练、步态训练等专业康复指导', 150.00, '次', 60, 1, 4),
('陪伴聊天', '陪伴', '专业陪护人员提供聊天解闷、读书读报、散步陪伴等精神慰藉服务', 60.00, '次', 120, 1, 5),
('代购代办', '其他', '协助老人购买日用品、代缴水电费、代办各类手续等便民服务', 40.00, '次', 90, 1, 6),
('洗浴护理', '护理', '协助老人完成洗浴，包含浴前准备、洗浴协助、浴后护理', 100.00, '次', 90, 1, 7),
('中医推拿', '康复', '持证中医师提供推拿按摩服务，缓解肌肉酸痛，促进血液循环', 130.00, '次', 60, 1, 8);

-- 服务订单
INSERT INTO `service_order` (`user_id`, `order_no`, `elderly_id`, `elderly_name`, `service_id`, `service_name`, `service_price`, `nurse_id`, `nurse_name`, `appointment_time`, `contact_phone`, `address`, `remark`, `status`, `complete_time`, `cancel_reason`, `cancel_type`, `cancel_time`) VALUES
(1, 'ORD20260501001', 1, '王大爷', 1, '营养助餐', 25.00, 3, '张护理', '2026-05-05 11:30:00', '13900000001', '幸福路100号3栋201', '少盐少油', 2, '2026-05-05 12:30:00', NULL, NULL, NULL),
(2, 'ORD20260501002', 2, '李奶奶', 3, '日常护理', 120.00, 4, '李护理', '2026-05-06 09:00:00', '13900000002', '和平街50号1单元302', '需要协助如厕', 2, '2026-05-06 12:00:00', NULL, NULL, NULL),
(1, 'ORD20260502001', 3, '张爷爷', 4, '康复理疗', 150.00, 3, '张护理', '2026-05-07 14:00:00', '13900000003', '建设路88号2栋101', '膝关节康复', 0, NULL, NULL, NULL, NULL),
(2, 'ORD20260502002', 4, '赵奶奶', 5, '陪伴聊天', 60.00, NULL, NULL, '2026-05-08 10:00:00', '13900000004', '人民路66号5栋402', NULL, 0, NULL, NULL, NULL, NULL),
(1, 'ORD20260503001', 5, '刘大爷', 2, '居家保洁', 80.00, 4, '李护理', '2026-05-04 15:00:00', '13900000005', '光明路22号3单元501', NULL, 2, '2026-05-04 17:00:00', NULL, NULL, NULL),
(1, 'ORD20260504001', 1, '王大爷', 6, '代购代办', 40.00, NULL, NULL, '2026-05-10 09:00:00', '13900000001', '幸福路100号3栋201', '购买日用品', 0, NULL, NULL, NULL, NULL),
(2, 'ORD20260504002', 2, '李奶奶', 7, '洗浴护理', 100.00, 4, '李护理', '2026-05-11 14:00:00', '13900000002', '和平街50号1单元302', NULL, 2, '2026-05-11 15:30:00', NULL, NULL, NULL),
(1, 'ORD20260505001', 3, '张爷爷', 4, '康复理疗', 150.00, 3, '张护理', '2026-05-12 14:00:00', '13900000003', '建设路88号2栋101', '继续康复训练', 1, NULL, NULL, NULL, NULL),
(2, 'ORD20260505002', 4, '赵奶奶', 5, '陪伴聊天', 60.00, 3, '张护理', '2026-05-13 10:00:00', '13900000004', '人民路66号5栋402', '下午聊天', 2, '2026-05-13 12:00:00', NULL, NULL, NULL),
(1, 'ORD20260506001', 5, '刘大爷', 8, '中医推拿', 130.00, NULL, NULL, '2026-05-14 15:00:00', '13900000005', '光明路22号3单元501', '肩颈酸痛', 3, NULL, '身体不适取消', 'manual', '2026-05-13 10:00:00');

-- 评价
INSERT INTO `evaluation` (`order_id`, `order_no`, `elderly_id`, `elderly_name`, `nurse_id`, `nurse_name`, `score`, `content`) VALUES
(1, 'ORD20260501001', 1, '王大爷', 3, '张护理', 5, '饭菜很合口味，服务态度很好！'),
(5, 'ORD20260503001', 5, '刘大爷', 4, '李护理', 4, '打扫得很干净，非常满意'),
(7, 'ORD20260504002', 2, '李奶奶', 4, '李护理', 5, '服务周到，非常满意！'),
(9, 'ORD20260505002', 4, '赵奶奶', 3, '张护理', 5, '聊天很愉快，心情好多了'),
(2, 'ORD20260501002', 2, '李奶奶', 4, '李护理', 4, '护理很专业，态度也好');

-- 健康记录
INSERT INTO `health_record` (`elderly_id`, `elderly_name`, `heart_rate`, `blood_pressure_high`, `blood_pressure_low`, `blood_sugar`, `temperature`, `weight`, `oxygen_saturation`, `mood`, `remark`, `record_time`, `recorder_id`, `recorder_name`) VALUES
(1, '王大爷', 72, 135, 85, 6.8, 36.5, 68.0, 97.50, 1, '血压偏高，注意饮食', '2026-05-09 08:00:00', 3, '张护理'),
(2, '李奶奶', 68, 128, 78, 5.6, 36.3, 55.0, 98.00, 2, '睡眠质量一般', '2026-05-09 08:30:00', 4, '李护理'),
(3, '张爷爷', 75, 142, 90, 7.2, 36.6, 72.0, 96.80, 1, '心率正常范围', '2026-05-09 09:00:00', 3, '张护理'),
(4, '赵奶奶', 70, 125, 75, 5.8, 36.4, 58.5, 97.80, 1, '精神状态良好', '2026-05-09 09:30:00', 3, '张护理'),
(5, '刘大爷', 68, 130, 80, 6.2, 36.5, 70.0, 98.20, 1, '血压控制良好', '2026-05-09 10:00:00', 4, '李护理'),
(1, '王大爷', 74, 138, 86, 7.0, 36.6, 68.2, 97.00, 2, '血压略升，已调整用药', '2026-05-10 08:00:00', 3, '张护理'),
(2, '李奶奶', 66, 126, 76, 5.5, 36.2, 54.8, 98.10, 1, '睡眠改善', '2026-05-10 08:30:00', 4, '李护理'),
(3, '张爷爷', 73, 138, 88, 7.1, 36.5, 71.8, 96.50, 1, '康复训练效果良好', '2026-05-10 09:00:00', 3, '张护理'),
(1, '王大爷', 70, 132, 82, 6.6, 36.4, 68.0, 97.60, 1, '血压平稳', '2026-05-11 08:00:00', 3, '张护理'),
(5, '刘大爷', 67, 128, 78, 6.0, 36.5, 69.8, 98.00, 2, '血糖正常', '2026-05-11 08:30:00', 4, '李护理');

-- 护理计划
INSERT INTO `care_plan` (`elderly_id`, `elderly_name`, `nurse_id`, `nurse_name`, `plan_name`, `care_type`, `content`, `frequency`, `start_date`, `end_date`, `status`) VALUES
(1, '王大爷', 3, '张护理', '高血压日常护理', '日常护理', '每日监测血压、督促服药、饮食指导', '每天', '2026-05-01', '2026-08-01', 1),
(2, '李奶奶', 4, '李护理', '全护理照护计划', '日常护理', '协助如厕、更换衣物、喂餐、体位转换', '每天', '2026-05-01', '2026-11-01', 1),
(3, '张爷爷', 3, '张护理', '膝关节康复计划', '康复护理', '关节活动训练、步态训练、热敷理疗', '每周', '2026-05-01', '2026-07-01', 1);

-- 公告资讯
INSERT INTO `notice` (`title`, `content`, `type`, `is_top`, `publish_time`, `status`, `author`) VALUES
('2026年端午节活动通知', '尊敬的各位老人及家属：\n\n为弘扬传统文化，丰富老年人的精神文化生活，我中心将于2026年5月31日（端午节当天）上午9:00-11:00在活动中心举办"浓情端午·粽享欢乐"主题活动。\n\n活动内容包括：\n1. 包粽子体验\n2. 香囊制作\n3. 端午知识问答\n4. 文艺表演\n\n欢迎各位老人及家属踊跃参加！请于5月28日前到前台报名。', 3, 1, '2026-05-10 09:00:00', 1, '管理员'),
('夏季健康饮食指南', '夏季天气炎热，老年人饮食需特别注意：\n\n1. 清淡为主：多食用蔬菜水果，减少油腻食物\n2. 补充水分：每天饮水不少于1500ml，少量多次\n3. 注意食品卫生：食物要新鲜，避免食用隔夜菜\n4. 适量补充蛋白质：鸡蛋、鱼肉、豆制品是良好来源\n5. 少食多餐：避免暴饮暴食，每餐七分饱为宜\n\n如有特殊饮食需求，请咨询我们的营养师。', 2, 0, '2026-05-08 10:00:00', 1, '健康顾问'),
('服务中心5月工作安排', '各位同事：\n\n5月份工作重点如下：\n1. 完成所有在院老人健康体检\n2. 更新护理计划，确保个性化服务\n3. 开展消防安全演练\n4. 组织护理技能培训\n5. 优化助餐服务流程\n\n请各部门按计划执行，确保服务质量。', 1, 0, '2026-05-01 08:00:00', 1, '管理员'),
('老年人防诈骗安全提醒', '近期电信诈骗案件频发，请各位老人注意防范：\n\n1. 不轻信陌生电话和短信\n2. 不随意点击不明链接\n3. 不向陌生人透露个人信息\n4. 遇到可疑情况及时联系家属或服务中心\n5. 安装国家反诈中心APP\n\n如遇诈骗，请立即拨打110报警。', 2, 0, '2026-05-05 14:00:00', 1, '安全顾问');

-- 轮播图
INSERT INTO `banner` (`title`, `image`, `link_url`, `sort_order`, `status`) VALUES
('智慧养老 温暖相伴', '/images/banner1.jpg', '/service', 1, 1),
('专业护理 贴心服务', '/images/banner2.jpg', '/service', 2, 1),
('健康生活 快乐晚年', '/images/banner3.jpg', '/news', 3, 1);

-- 角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`, `sort_order`) VALUES
('超级管理员', 'SUPER_ADMIN', '系统最高权限管理员', 1, 1),
('普通管理员', 'ADMIN', '日常管理权限', 1, 2),
('护理员', 'NURSE', '老人护理服务', 1, 3),
('医生', 'DOCTOR', '医疗诊断服务', 1, 4),
('营养师', 'DIETITIAN', '营养膳食指导', 1, 5),
('康复师', 'THERAPIST', '康复理疗服务', 1, 6),
('保洁员', 'CLEANER', '环境卫生维护', 1, 7);

-- 用药计划
INSERT INTO `medication_plan` (`elderly_id`, `elderly_name`, `medicine_name`, `dosage`, `frequency`, `times_per_day`, `take_times`, `start_date`, `end_date`, `doctor_id`, `doctor_name`, `prescription_no`, `remark`, `status`) VALUES
(1, '王大爷', '硝苯地平缓释片', '1片', '每天', 2, '08:00,18:00', '2026-05-01', '2026-08-01', 3, '张医生', 'RX20260501001', '高血压用药，不可随意停药', 1),
(1, '王大爷', '二甲双胍', '1片', '每天', 3, '08:00,12:00,18:00', '2026-05-01', '2026-08-01', 3, '张医生', 'RX20260501002', '糖尿病用药', 1),
(2, '李奶奶', '钙片', '2片', '每天', 1, '20:00', '2026-05-01', '2026-11-01', NULL, NULL, NULL, '骨质疏松补钙', 1),
(3, '张爷爷', '硝酸甘油', '1片', '每天', 1, '08:00', '2026-05-01', '2026-07-01', 3, '张医生', 'RX20260501003', '冠心病用药', 1);

-- 用药记录
INSERT INTO `medication_record` (`plan_id`, `elderly_id`, `elderly_name`, `medicine_name`, `dosage`, `take_time`, `status`, `recorder_id`, `recorder_name`, `remark`) VALUES
(1, 1, '王大爷', '硝苯地平缓释片', '1片', '2026-05-09 08:00:00', 1, 3, '张护理', NULL),
(1, 1, '王大爷', '硝苯地平缓释片', '1片', '2026-05-09 18:00:00', 1, 3, '张护理', NULL),
(2, 1, '王大爷', '二甲双胍', '1片', '2026-05-09 08:00:00', 1, 3, '张护理', NULL),
(2, 1, '王大爷', '二甲双胍', '1片', '2026-05-09 12:00:00', 2, NULL, NULL, '漏服'),
(3, 2, '李奶奶', '钙片', '2片', '2026-05-09 20:00:00', 1, 4, '李护理', NULL),
(4, 3, '张爷爷', '硝酸甘油', '1片', '2026-05-09 08:00:00', 1, 3, '张护理', NULL),
(1, 1, '王大爷', '硝苯地平缓释片', '1片', '2026-05-10 08:00:00', 1, 3, '张护理', NULL),
(1, 1, '王大爷', '硝苯地平缓释片', '1片', '2026-05-10 18:00:00', 1, 3, '张护理', NULL),
(2, 1, '王大爷', '二甲双胍', '1片', '2026-05-10 08:00:00', 1, 3, '张护理', NULL),
(2, 1, '王大爷', '二甲双胍', '1片', '2026-05-10 12:00:00', 1, 3, '张护理', NULL),
(2, 1, '王大爷', '二甲双胍', '1片', '2026-05-10 18:00:00', 1, 3, '张护理', NULL),
(3, 2, '李奶奶', '钙片', '2片', '2026-05-10 20:00:00', 1, 4, '李护理', NULL),
(4, 3, '张爷爷', '硝酸甘油', '1片', '2026-05-10 08:00:00', 1, 3, '张护理', NULL),
(1, 1, '王大爷', '硝苯地平缓释片', '1片', '2026-05-11 08:00:00', 1, 3, '张护理', NULL),
(3, 2, '李奶奶', '钙片', '2片', '2026-05-11 20:00:00', 3, NULL, NULL, '拒服');

-- 紧急呼叫
INSERT INTO `emergency_call` (`elderly_id`, `elderly_name`, `room_number`, `call_time`, `call_type`, `status`, `responder_id`, `responder_name`, `response_time`, `handle_result`, `location_lat`, `location_lng`) VALUES
(2, '李奶奶', '101', '2026-05-08 14:30:00', 'emergency', 2, 4, '李护理', '2026-05-08 14:32:00', '老人突发头晕，已送医检查，无大碍', 30.5728, 104.0668),
(1, '王大爷', '101', '2026-05-06 09:15:00', 'health', 2, 3, '张护理', '2026-05-06 09:18:00', '血压偏高，已调整用药', 30.5728, 104.0668),
(4, '赵奶奶', '102', '2026-05-09 16:00:00', 'other', 1, 3, '张护理', '2026-05-09 16:05:00', '老人想找人聊天', 30.5729, 104.0669),
(3, '张爷爷', '102', '2026-05-11 10:30:00', 'fall', 2, 3, '张护理', '2026-05-11 10:35:00', '老人不慎跌倒，已检查无骨折', 30.5729, 104.0669),
(5, '刘大爷', '103', '2026-05-12 22:15:00', 'emergency', 2, 4, '李护理', '2026-05-12 22:20:00', '胸闷不适，已送医', 30.5730, 104.0670),
(2, '李奶奶', '101', '2026-05-13 15:00:00', 'health', 1, 4, '李护理', '2026-05-13 15:05:00', '血糖偏低，已补充糖分', 30.5728, 104.0668),
(1, '王大爷', '101', '2026-05-14 08:45:00', 'other', 2, 3, '张护理', '2026-05-14 08:50:00', '呼叫维修空调', 30.5728, 104.0668);

-- 房间
INSERT INTO `room` (`room_number`, `floor`, `room_type`, `bed_count`, `window_direction`, `facility`, `status`, `remark`) VALUES
('101', 1, 'double', 2, '南', '空调、电视、独立卫生间、热水器', 1, '朝南采光好'),
('102', 1, 'double', 2, '东南', '空调、电视、独立卫生间、热水器', 1, NULL),
('103', 1, 'double', 2, '北', '空调、电视、独立卫生间、热水器', 1, NULL),
('104', 1, 'single', 1, '南', '空调、电视、独立卫生间、热水器、沙发', 1, '单人间'),
('201', 2, 'double', 2, '南', '空调、电视、独立卫生间、热水器', 1, NULL),
('202', 2, 'double', 2, '东南', '空调、电视、独立卫生间、热水器', 1, NULL),
('203', 2, 'triple', 3, '北', '空调、电视、公共卫生间', 1, NULL),
('301', 3, 'suite', 2, '东南', '空调、电视、独立卫生间、热水器、客厅', 1, '套房');

-- 床位
INSERT INTO `bed` (`room_id`, `room_number`, `bed_number`, `elderly_id`, `elderly_name`, `check_in_time`, `check_out_time`, `status`, `remark`) VALUES
(1, '101', '1', 1, '王大爷', '2026-03-01 08:00:00', NULL, 1, NULL),
(1, '101', '2', 2, '李奶奶', '2026-03-01 08:00:00', NULL, 1, NULL),
(2, '102', '1', 3, '张爷爷', '2026-04-01 08:00:00', NULL, 1, NULL),
(2, '102', '2', 4, '赵奶奶', '2026-04-01 08:00:00', NULL, 1, NULL),
(3, '103', '1', 5, '刘大爷', '2026-05-01 08:00:00', NULL, 1, NULL),
(3, '103', '2', NULL, NULL, NULL, NULL, 0, NULL),
(4, '104', '1', NULL, NULL, NULL, NULL, 0, NULL),
(5, '201', '1', NULL, NULL, NULL, NULL, 0, NULL),
(5, '201', '2', NULL, NULL, NULL, NULL, 0, NULL),
(6, '202', '1', NULL, NULL, NULL, NULL, 0, NULL),
(6, '202', '2', NULL, NULL, NULL, NULL, 0, NULL),
(7, '203', '1', NULL, NULL, NULL, NULL, 0, NULL),
(7, '203', '2', NULL, NULL, NULL, NULL, 0, NULL),
(7, '203', '3', NULL, NULL, NULL, NULL, 0, NULL),
(8, '301', '1', NULL, NULL, NULL, NULL, 0, NULL),
(8, '301', '2', NULL, NULL, NULL, NULL, 0, NULL);

-- 费用项目
INSERT INTO `fee_item` (`item_name`, `fee_type`, `price`, `unit`, `description`, `status`, `sort_order`) VALUES
('床位费（双人间）', 'monthly', 1800.00, '月', '双人间床位费用', 1, 1),
('床位费（单人间）', 'monthly', 2800.00, '月', '单人间床位费用', 1, 2),
('床位费（套房）', 'monthly', 5000.00, '月', '套房床位费用', 1, 3),
('护理费（自理）', 'monthly', 800.00, '月', '自理老人护理费', 1, 4),
('护理费（半护理）', 'monthly', 1500.00, '月', '半护理老人护理费', 1, 5),
('护理费（全护理）', 'monthly', 2500.00, '月', '全护理老人护理费', 1, 6),
('餐饮费', 'monthly', 1200.00, '月', '三餐饮食费用', 1, 7),
('水电费', 'monthly', 200.00, '月', '水电气费用', 1, 8),
('洗衣费', 'monthly', 100.00, '月', '衣物清洗费用', 1, 9),
('康复理疗费', 'per_time', 150.00, '次', '康复理疗服务费用', 1, 10),
('洗浴护理费', 'per_time', 100.00, '次', '洗浴护理服务费用', 1, 11);

-- 费用账单
INSERT INTO `fee_bill` (`bill_no`, `elderly_id`, `elderly_name`, `bill_month`, `total_amount`, `paid_amount`, `status`, `due_date`, `remark`) VALUES
('BILL202605001', 1, '王大爷', '2026-05', 3500.00, 3500.00, 2, '2026-05-15', NULL),
('BILL202605002', 2, '李奶奶', '2026-05', 4500.00, 4000.00, 1, '2026-05-15', '部分支付'),
('BILL202605003', 3, '张爷爷', '2026-05', 3500.00, 0.00, 0, '2026-05-15', NULL),
('BILL202605004', 4, '赵奶奶', '2026-05', 3000.00, 3000.00, 2, '2026-05-15', NULL),
('BILL202605005', 5, '刘大爷', '2026-05', 3300.00, 1500.00, 1, '2026-05-15', NULL);

-- 费用明细
INSERT INTO `fee_detail` (`bill_id`, `bill_no`, `item_id`, `item_name`, `quantity`, `unit_price`, `amount`, `remark`) VALUES
(1, 'BILL202605001', 1, '床位费（双人间）', 1, 1800.00, 1800.00, NULL),
(1, 'BILL202605001', 5, '护理费（半护理）', 1, 1500.00, 1500.00, NULL),
(1, 'BILL202605001', 7, '餐饮费', 1, 1200.00, 1200.00, NULL),
(1, 'BILL202605001', 8, '水电费', 1, 200.00, 200.00, NULL),
(2, 'BILL202605002', 1, '床位费（双人间）', 1, 1800.00, 1800.00, NULL),
(2, 'BILL202605002', 6, '护理费（全护理）', 1, 2500.00, 2500.00, NULL),
(2, 'BILL202605002', 7, '餐饮费', 1, 1200.00, 1200.00, NULL),
(3, 'BILL202605003', 1, '床位费（双人间）', 1, 1800.00, 1800.00, NULL),
(3, 'BILL202605003', 5, '护理费（半护理）', 1, 1500.00, 1500.00, NULL),
(3, 'BILL202605003', 7, '餐饮费', 1, 1200.00, 1200.00, NULL),
(4, 'BILL202605004', 1, '床位费（双人间）', 1, 1800.00, 1800.00, NULL),
(4, 'BILL202605004', 4, '护理费（自理）', 1, 800.00, 800.00, NULL),
(4, 'BILL202605004', 7, '餐饮费', 1, 1200.00, 1200.00, NULL),
(5, 'BILL202605005', 1, '床位费（双人间）', 1, 1800.00, 1800.00, NULL),
(5, 'BILL202605005', 4, '护理费（自理）', 1, 800.00, 800.00, NULL),
(5, 'BILL202605005', 7, '餐饮费', 1, 1200.00, 1200.00, NULL),
(5, 'BILL202605005', 8, '水电费', 1, 200.00, 200.00, NULL),
(5, 'BILL202605005', 9, '洗衣费', 1, 100.00, 100.00, NULL);

-- 支付记录
INSERT INTO `payment` (`pay_no`, `bill_id`, `bill_no`, `elderly_id`, `elderly_name`, `pay_amount`, `pay_method`, `pay_time`, `status`, `transaction_id`, `remark`) VALUES
('PAY202605001', 1, 'BILL202605001', 1, '王大爷', 3500.00, 'wechat', '2026-05-10 10:30:00', 2, 'wx20260510001', NULL),
('PAY202605002', 2, 'BILL202605002', 2, '李奶奶', 4000.00, 'alipay', '2026-05-12 14:00:00', 2, 'ali20260512001', '剩余500元月底结清'),
('PAY202605003', 4, 'BILL202605004', 4, '赵奶奶', 3000.00, 'cash', '2026-05-08 09:00:00', 2, NULL, '现金支付'),
('PAY202605004', 5, 'BILL202605005', 5, '刘大爷', 1500.00, 'bank', '2026-05-14 16:30:00', 2, 'bank20260514001', NULL),
('PAY202605005', 3, 'BILL202605003', 3, '张爷爷', 3500.00, 'wechat', NULL, 0, NULL, '待支付');

-- 活动
INSERT INTO `activity` (`activity_name`, `activity_type`, `description`, `start_time`, `end_time`, `location`, `max_participants`, `current_participants`, `cover_image`, `status`, `organizer_id`, `organizer_name`, `remark`) VALUES
('端午节包粽子活动', 'culture', '弘扬传统文化，组织老人包粽子，共度佳节', '2026-05-31 09:00:00', '2026-05-31 11:30:00', '活动中心', 30, 15, '/images/activity1.jpg', 1, 2, '张管理', NULL),
('太极拳晨练', 'sports', '专业教练指导老人练习太极拳，强身健体', '2026-06-01 07:00:00', '2026-06-01 08:30:00', '室外广场', 40, 20, '/images/activity2.jpg', 1, 3, '张护理', '每周一、三、五'),
('健康知识讲座-夏季养生', 'health', '邀请医生讲解夏季老年人养生知识', '2026-06-05 14:00:00', '2026-06-05 16:00:00', '会议室', 50, 35, '/images/activity3.jpg', 1, 3, '张医生', NULL),
('志愿者陪伴聊天', 'volunteer', '组织志愿者与老人聊天互动，陪伴解闷', '2026-06-08 10:00:00', '2026-06-08 12:00:00', '各楼层活动室', 20, 10, '/images/activity4.jpg', 1, 2, '张管理', NULL),
('书法兴趣班', 'culture', '书法老师指导老人练习书法，陶冶情操', '2026-06-10 09:00:00', '2026-06-10 11:00:00', '书画室', 15, 8, '/images/activity5.jpg', 0, 2, '张管理', '待发布');

-- 活动报名
INSERT INTO `activity_signup` (`activity_id`, `activity_name`, `elderly_id`, `elderly_name`, `signup_time`, `status`, `sign_in_time`, `remark`) VALUES
(1, '端午节包粽子活动', 1, '王大爷', '2026-05-25 10:00:00', 2, '2026-05-31 08:55:00', NULL),
(1, '端午节包粽子活动', 2, '李奶奶', '2026-05-26 14:00:00', 1, NULL, NULL),
(1, '端午节包粽子活动', 3, '张爷爷', '2026-05-27 09:00:00', 0, NULL, NULL),
(2, '太极拳晨练', 1, '王大爷', '2026-05-28 08:00:00', 2, '2026-06-01 06:55:00', NULL),
(2, '太极拳晨练', 5, '刘大爷', '2026-05-29 08:30:00', 1, NULL, NULL),
(3, '健康知识讲座-夏季养生', 1, '王大爷', '2026-06-01 10:00:00', 0, NULL, NULL),
(3, '健康知识讲座-夏季养生', 2, '李奶奶', '2026-06-02 11:00:00', 0, NULL, NULL),
(3, '健康知识讲座-夏季养生', 4, '赵奶奶', '2026-06-03 09:00:00', 1, NULL, NULL),
(4, '志愿者陪伴聊天', 4, '赵奶奶', '2026-06-05 14:00:00', 0, NULL, NULL),
(5, '书法兴趣班', 3, '张爷爷', '2026-06-08 10:00:00', 0, NULL, '有书法基础');

-- 消息通知
INSERT INTO `tb_message` (`user_id`, `target_type`, `type`, `title`, `content`, `read_flag`) VALUES
(0, 'user', 'system', 'Welcome to Smart Elderly Care Platform', 'Thank you for registering!', 0),
(0, 'user', 'system', 'Maintenance Notice', 'System maintenance tonight 22:00-00:00', 0),
(1, 'user', 'order', '订单已完成', '您的订单 ORD20260501001 营养助餐服务已完成', 1),
(1, 'user', 'order', '订单待支付', '您有新的订单 ORD20260504001 待处理', 0),
(2, 'user', 'order', '订单已分配', '您的订单 ORD20260501002 已分配给护理员李护理', 0),
(1, 'user', 'service', '服务提醒', '王大爷的康复理疗预约时间为今天下午14:00', 0),
(2, 'user', 'service', '用药提醒', '李奶奶的钙片需要在今晚20:00服用', 1),
(0, 'admin', 'system', '紧急呼叫通知', '李奶奶于2026-05-08 14:30发起紧急呼叫', 1),
(0, 'admin', 'system', '服务超时警告', '订单 ORD20260506001 已超时自动取消', 0),
(1, 'user', 'other', '活动邀请', '端午节包粽子活动将于5月31日举行，欢迎参加', 0),
(2, 'user', 'other', '健康报告', '李奶奶本周健康监测报告已生成，请查看', 1);
