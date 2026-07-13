-- 服务订单表添加取消类型和取消时间字段
ALTER TABLE service_order 
ADD COLUMN cancel_type VARCHAR(20) DEFAULT 'manual' COMMENT '取消类型：manual-用户自主取消，admin-管理员取消，timeout-超时自动取消' AFTER cancel_reason,
ADD COLUMN cancel_time DATETIME COMMENT '取消时间' AFTER cancel_type;

-- 创建索引
CREATE INDEX idx_cancel_type ON service_order(cancel_type);
CREATE INDEX idx_cancel_time ON service_order(cancel_time);
