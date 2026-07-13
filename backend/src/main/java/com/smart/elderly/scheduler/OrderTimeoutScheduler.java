package com.smart.elderly.scheduler;

import com.smart.elderly.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单超时调度器
 * 处理超过30分钟未受理的订单，自动取消
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderTimeoutScheduler {

    private final ServiceOrderService serviceOrderService;

    /**
     * 每5分钟检查一次超时订单
     * 订单创建后超过30分钟未派单则自动取消
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void processTimeoutOrders() {
        log.info("开始处理超时订单...");
        try {
            serviceOrderService.processTimeoutOrders();
            log.info("超时订单处理完成");
        } catch (Exception e) {
            log.error("处理超时订单失败", e);
        }
    }
}
