package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.elderly.entity.Message;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 获取客户端用户消息列表（分页）
     */
    Page<Message> getAppUserMessages(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取客户端用户未读消息数量
     */
    Integer getAppUserUnreadCount(Long userId);

    /**
     * 标记客户端用户单条消息已读
     */
    void markAppMessageRead(Long userId, Long messageId);

    /**
     * 标记客户端用户所有消息已读
     */
    void markAllAppMessagesRead(Long userId);

    /**
     * 删除客户端用户消息
     */
    void deleteAppMessage(Long userId, Long messageId);

    /**
     * 发送消息给客户端用户
     */
    void sendMessageToUser(Long userId, String type, String title, String content);

    /**
     * 发送带订单关联的消息给客户端用户
     */
    void sendOrderMessageToUser(Long userId, Long orderId, String type, String title, String content);

    /**
     * 发送系统消息（广播给所有客户端用户）
     */
    void sendSystemMessage(String title, String content);

    /**
     * 获取管理员消息列表（分页）
     */
    Page<Message> getAdminMessages(Integer pageNum, Integer pageSize);

    /**
     * 获取管理员未读消息数量
     */
    Integer getAdminUnreadCount();

    /**
     * 标记管理员单条消息已读
     */
    void markAdminMessageRead(Long messageId);

    /**
     * 标记管理员所有消息已读
     */
    void markAllAdminMessagesRead();

    /**
     * 删除管理员消息
     */
    void deleteAdminMessage(Long messageId);

    /**
     * 发送消息给管理员
     */
    void sendMessageToAdmin(String type, String title, String content);

    /**
     * 发送带订单关联的消息给管理员
     */
    void sendOrderMessageToAdmin(Long orderId, String type, String title, String content);
}