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
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
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
-- 初始化数据
-- ============================================================

-- 管理员账号（密码：123456，BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('admin', '$2a$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '系统管理员', '13800000000', 1, 1),
('manager', '$2a$10$YBVB5qTYI/izetFTSQMyM./HhmdGVPc6vrSiHpodT81A05lOL1ONO', '张管理', '13800000001', 2, 1),
('nurse1', '$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '张护理', '13800000002', 3, 1),
('nurse2', '$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '李护理', '13800000003', 3, 1);

-- 客户端用户测试账号（密码：123456）
INSERT INTO `app_user` (`username`, `password`, `real_name`, `phone`, `gender`, `age`, `address`, `emergency_contact`, `emergency_phone`, `health_status`, `status`) VALUES
('user1', '$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '王明', '13900000001', 1, 45, '幸福路100号3栋201', '王大爷', '13700000001', '良好', 1),
('user2', '$10$Cb7RzenP.Ae3UWqNffVwGuHtauET4qe.VbLZrvXsUHVGleYIJMNWC', '李华', '13900000002', 0, 38, '和平街50号1单元302', '李奶奶', '13700000002', '一般', 1);

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
INSERT INTO `service_order` (`order_no`, `elderly_id`, `elderly_name`, `service_id`, `service_name`, `service_price`, `nurse_id`, `nurse_name`, `appointment_time`, `contact_phone`, `address`, `remark`, `status`) VALUES
('ORD20260501001', 1, '王大爷', 1, '营养助餐', 25.00, 3, '张护理', '2026-05-05 11:30:00', '13900000001', '幸福路100号3栋201', '少盐少油', 2),
('ORD20260501002', 2, '李奶奶', 3, '日常护理', 120.00, 4, '李护理', '2026-05-06 09:00:00', '13900000002', '和平街50号1单元302', '需要协助如厕', 1),
('ORD20260502001', 3, '张爷爷', 4, '康复理疗', 150.00, 3, '张护理', '2026-05-07 14:00:00', '13900000003', '建设路88号2栋101', '膝关节康复', 0),
('ORD20260502002', 4, '赵奶奶', 5, '陪伴聊天', 60.00, NULL, NULL, '2026-05-08 10:00:00', '13900000004', '人民路66号5栋402', NULL, 0),
('ORD20260503001', 5, '刘大爷', 2, '居家保洁', 80.00, 4, '李护理', '2026-05-04 15:00:00', '13900000005', '光明路22号3单元501', NULL, 2);

-- 评价
INSERT INTO `evaluation` (`order_id`, `order_no`, `elderly_id`, `elderly_name`, `nurse_id`, `nurse_name`, `score`, `content`) VALUES
(1, 'ORD20260501001', 1, '王大爷', 3, '张护理', 5, '饭菜很合口味，服务态度很好！'),
(5, 'ORD20260503001', 5, '刘大爷', 4, '李护理', 4, '打扫得很干净，非常满意');

-- 健康记录
INSERT INTO `health_record` (`elderly_id`, `elderly_name`, `heart_rate`, `blood_pressure_high`, `blood_pressure_low`, `blood_sugar`, `temperature`, `weight`, `oxygen_saturation`, `mood`, `remark`, `record_time`, `recorder_id`, `recorder_name`) VALUES
(1, '王大爷', 72, 135, 85, 6.8, 36.5, 68.0, 97.50, 1, '血压偏高，注意饮食', '2026-05-09 08:00:00', 3, '张护理'),
(2, '李奶奶', 68, 128, 78, 5.6, 36.3, 55.0, 98.00, 2, '睡眠质量一般', '2026-05-09 08:30:00', 4, '李护理'),
(3, '张爷爷', 75, 142, 90, 7.2, 36.6, 72.0, 96.80, 1, '心率正常范围', '2026-05-09 09:00:00', 3, '张护理');

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


ALTER TABLE service_order 
ADD COLUMN cancel_type VARCHAR(20) DEFAULT 'manual' COMMENT '取消类型' AFTER cancel_reason,
ADD COLUMN cancel_time DATETIME COMMENT '取消时间' AFTER cancel_type;
