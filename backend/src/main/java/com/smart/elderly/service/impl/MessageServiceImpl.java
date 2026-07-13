package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.elderly.entity.AppUser;
import com.smart.elderly.entity.Message;
import com.smart.elderly.mapper.AppUserMapper;
import com.smart.elderly.mapper.MessageMapper;
import com.smart.elderly.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private AppUserMapper appUserMapper;

    @Override
    public Page<Message> getAppUserMessages(Long userId, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getUserId, userId).or().eq(Message::getUserId, 0))
                .eq(Message::getTargetType, "user")
                .orderByDesc(Message::getCreateTime);
        return messageMapper.selectPage(page, wrapper);
    }

    @Override
    public Integer getAppUserUnreadCount(Long userId) {
        return messageMapper.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAppMessageRead(Long userId, Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message != null && "user".equals(message.getTargetType()) && 
            (message.getUserId().equals(userId) || message.getUserId() == 0)) {
            message.setReadFlag(1);
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional
    public void markAllAppMessagesRead(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getUserId, userId).or().eq(Message::getUserId, 0))
                .eq(Message::getTargetType, "user")
                .eq(Message::getReadFlag, 0);
        Message updateMsg = new Message();
        updateMsg.setReadFlag(1);
        messageMapper.update(updateMsg, wrapper);
    }

    @Override
    @Transactional
    public void deleteAppMessage(Long userId, Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message != null && "user".equals(message.getTargetType()) && 
            (message.getUserId().equals(userId) || message.getUserId() == 0)) {
            messageMapper.deleteById(messageId);
        }
    }

    @Override
    @Transactional
    public void sendMessageToUser(Long userId, String type, String title, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTargetType("user");
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(0);
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void sendOrderMessageToUser(Long userId, Long orderId, String type, String title, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTargetType("user");
        message.setOrderId(orderId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(0);
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void sendSystemMessage(String title, String content) {
        Message message = new Message();
        message.setUserId(0L);
        message.setTargetType("user");
        message.setType("system");
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(0);
        messageMapper.insert(message);
    }

    @Override
    public Page<Message> getAdminMessages(Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getTargetType, "admin")
                .orderByDesc(Message::getCreateTime);
        return messageMapper.selectPage(page, wrapper);
    }

    @Override
    public Integer getAdminUnreadCount() {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getTargetType, "admin")
                .eq(Message::getReadFlag, 0);
        return messageMapper.selectCount(wrapper).intValue();
    }

    @Override
    @Transactional
    public void markAdminMessageRead(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message != null && "admin".equals(message.getTargetType())) {
            message.setReadFlag(1);
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional
    public void markAllAdminMessagesRead() {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getTargetType, "admin")
                .eq(Message::getReadFlag, 0);
        Message updateMsg = new Message();
        updateMsg.setReadFlag(1);
        messageMapper.update(updateMsg, wrapper);
    }

    @Override
    @Transactional
    public void deleteAdminMessage(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message != null && "admin".equals(message.getTargetType())) {
            messageMapper.deleteById(messageId);
        }
    }

    @Override
    @Transactional
    public void sendMessageToAdmin(String type, String title, String content) {
        Message message = new Message();
        message.setUserId(0L);
        message.setTargetType("admin");
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(0);
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void sendOrderMessageToAdmin(Long orderId, String type, String title, String content) {
        Message message = new Message();
        message.setUserId(0L);
        message.setTargetType("admin");
        message.setOrderId(orderId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setReadFlag(0);
        messageMapper.insert(message);
    }
}
