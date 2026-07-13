CREATE TABLE IF NOT EXISTS tb_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL DEFAULT 0 COMMENT '接收用户ID（客户端用户ID，为0表示系统消息）',
    target_type VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '消息接收者类型：user-客户端用户，admin-管理员',
    order_id BIGINT DEFAULT 0 COMMENT '关联订单ID',
    type VARCHAR(20) NOT NULL DEFAULT 'other' COMMENT '消息类型：system-系统通知，order-订单消息，service-服务提醒，other-其他',
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    read_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_target_type (target_type),
    INDEX idx_order_id (order_id),
    INDEX idx_read_flag (read_flag),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO tb_message (user_id, type, title, content, read_flag) VALUES
(0, 'system', 'Welcome to Smart Elderly Care Platform', 'Thank you for registering!', 0),
(0, 'system', 'Maintenance Notice', 'System maintenance tonight 22:00-00:00', 0);