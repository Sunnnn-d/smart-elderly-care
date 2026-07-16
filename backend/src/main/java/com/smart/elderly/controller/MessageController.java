package com.smart.elderly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.elderly.common.PageRequest;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.entity.Message;
import com.smart.elderly.service.MessageService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 获取客户端用户消息列表
     */
    @GetMapping("/app-user/list")
    public Result<PageResult<Message>> getAppUserMessages(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Page<Message> page = messageService.getAppUserMessages(userId, pageNum, pageSize);
        return Result.success(PageResult.from(page));
    }

    /**
     * 获取客户端用户未读消息数
     */
    @GetMapping("/app-user/unread-count")
    public Result<Integer> getAppUserUnreadCount(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Integer count = messageService.getAppUserUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条消息已读
     */
    @PutMapping("/app-user/{messageId}/read")
    public Result<Void> markAppMessageRead(HttpServletRequest request, @PathVariable Long messageId) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        messageService.markAppMessageRead(userId, messageId);
        return Result.success();
    }

    /**
     * 标记所有消息已读
     */
    @PutMapping("/app-user/all/read")
    public Result<Void> markAllAppMessagesRead(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        messageService.markAllAppMessagesRead(userId);
        return Result.success();
    }

    /**
     * 删除客户端用户消息
     */
    @DeleteMapping("/app-user/{messageId}")
    public Result<Void> deleteAppMessage(HttpServletRequest request, @PathVariable Long messageId) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        messageService.deleteAppMessage(userId, messageId);
        return Result.success();
    }

    /**
     * 发送消息给客户端用户（管理端）
     */
    @PostMapping("/send-to-user")
    public Result<Void> sendMessageToUser(@RequestBody Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        String type = (String) params.get("type");
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        messageService.sendMessageToUser(userId, type, title, content);
        return Result.success();
    }

    /**
     * 发送系统消息（广播）
     */
    @PostMapping("/send-system")
    public Result<Void> sendSystemMessage(@RequestBody Map<String, Object> params) {
        String title = (String) params.get("title");
        String content = (String) params.get("content");
        messageService.sendSystemMessage(title, content);
        return Result.success();
    }

    /**
     * 获取消息类型列表
     */
    @GetMapping("/types")
    public Result<Map<String, String>> getMessageTypes() {
        Map<String, String> types = new HashMap<>();
        types.put("system", "系统通知");
        types.put("order", "订单消息");
        types.put("service", "服务提醒");
        types.put("other", "其他");
        return Result.success(types);
    }

    // ==================== 管理员消息接口 ====================

    /**
     * 获取管理员消息列表
     */
    @GetMapping("/admin/list")
    public Result<PageResult<Message>> getAdminMessages(@RequestParam(defaultValue = "1") Integer pageNum,
                                                        @RequestParam(defaultValue = "20") Integer pageSize) {
        Page<Message> page = messageService.getAdminMessages(pageNum, pageSize);
        return Result.success(PageResult.from(page));
    }

    /**
     * 获取管理员未读消息数
     */
    @GetMapping("/admin/unread-count")
    public Result<Integer> getAdminUnreadCount() {
        Integer count = messageService.getAdminUnreadCount();
        return Result.success(count);
    }

    /**
     * 标记管理员单条消息已读
     */
    @PutMapping("/admin/{messageId}/read")
    public Result<Void> markAdminMessageRead(@PathVariable Long messageId) {
        messageService.markAdminMessageRead(messageId);
        return Result.success();
    }

    /**
     * 标记管理员所有消息已读
     */
    @PutMapping("/admin/all/read")
    public Result<Void> markAllAdminMessagesRead() {
        messageService.markAllAdminMessagesRead();
        return Result.success();
    }

    /**
     * 删除管理员消息
     */
    @DeleteMapping("/admin/{messageId}")
    public Result<Void> deleteAdminMessage(@PathVariable Long messageId) {
        messageService.deleteAdminMessage(messageId);
        return Result.success();
    }

    /**
     * 消息服务健康检查
     */
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("消息服务正常");
    }

    /**
     * 清理过期消息
     */
    @DeleteMapping("/clean-expired")
    public Result<Void> cleanExpiredMessages(@RequestParam(defaultValue = "30") Integer days) {
        messageService.cleanExpiredMessages(days);
        return Result.success();
    }
}