package com.smart.elderly.consumer;

import com.smart.elderly.config.RabbitMQConfig;
import com.smart.elderly.dto.OrderNotifyDTO;
import com.smart.elderly.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotifyConsumer {

    private final MessageService messageService;

    private final ConcurrentHashMap<String, Boolean> processedMessages = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitMQConfig.ORDER_NOTIFY_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleOrderNotify(OrderNotifyDTO dto) {
        log.info("收到订单通知消息: orderId={}, eventType={}, targetType={}", 
                dto.getOrderId(), dto.getEventType(), dto.getTargetType());

        String messageKey = dto.getOrderId() + ":" + dto.getEventType();
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            log.info("消息已处理过，跳过: {}", messageKey);
            return;
        }

        try {
            if ("admin".equals(dto.getTargetType())) {
                messageService.sendOrderMessageToAdmin(dto.getOrderId(), dto.getType(), dto.getTitle(), dto.getContent());
            } else if ("user".equals(dto.getTargetType())) {
                messageService.sendOrderMessageToUser(dto.getUserId(), dto.getOrderId(), dto.getType(), dto.getTitle(), dto.getContent());
            }
            log.info("订单通知消息处理成功: {}", messageKey);
        } catch (Exception e) {
            log.error("处理订单通知消息失败: {}", messageKey, e);
            processedMessages.remove(messageKey);
            throw e;
        }
    }
}