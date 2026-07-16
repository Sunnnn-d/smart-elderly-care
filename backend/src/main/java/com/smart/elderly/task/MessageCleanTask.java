package com.smart.elderly.task;

import com.smart.elderly.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageCleanTask {

    private final MessageService messageService;

    public MessageCleanTask(MessageService messageService) {
        this.messageService = messageService;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredMessages() {
        log.info("开始执行消息清理定时任务");
        try {
            messageService.cleanExpiredMessages(30);
            log.info("消息清理定时任务执行完成");
        } catch (Exception e) {
            log.error("消息清理定时任务执行失败", e);
        }
    }
}