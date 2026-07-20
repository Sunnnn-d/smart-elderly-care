package com.smart.elderly.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.RabbitMQConfig;
import com.smart.elderly.dto.AppointmentDTO;
import com.smart.elderly.dto.DispatchDTO;
import com.smart.elderly.dto.OrderNotifyDTO;
import com.smart.elderly.dto.OrderQueryDTO;
import com.smart.elderly.entity.Evaluation;
import com.smart.elderly.entity.ServiceItem;
import com.smart.elderly.entity.ServiceOrder;
import com.smart.elderly.mapper.EvaluationMapper;
import com.smart.elderly.mapper.ServiceItemMapper;
import com.smart.elderly.mapper.ServiceOrderMapper;
import com.smart.elderly.service.MessageService;
import com.smart.elderly.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {

    private final EvaluationMapper evaluationMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final MessageService messageService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    @Override
    public Result<PageResult<ServiceOrder>> pageList(OrderQueryDTO dto) {
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getElderlyName())) {
            wrapper.like(ServiceOrder::getElderlyName, dto.getElderlyName());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(ServiceOrder::getStatus, dto.getStatus());
        }
        if (StringUtils.hasText(dto.getOrderNo())) {
            wrapper.like(ServiceOrder::getOrderNo, dto.getOrderNo());
        }
        wrapper.orderByDesc(ServiceOrder::getCreateTime);

        Page<ServiceOrder> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        PageResult<ServiceOrder> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createOrder(AppointmentDTO dto) {
        // 查询服务项目信息
        ServiceItem serviceItem = serviceItemMapper.selectById(dto.getServiceId());
        if (serviceItem == null) {
            return Result.error("服务项目不存在");
        }

        ServiceOrder order = new ServiceOrder();
        // 生成订单编号
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%03d", IdUtil.getSnowflakeNextId() % 1000);
        order.setOrderNo(orderNo);
        order.setUserId(dto.getUserId());
        order.setElderlyName(dto.getElderlyName());
        order.setServiceId(dto.getServiceId());
        order.setServiceName(serviceItem.getName());
        order.setServicePrice(serviceItem.getPrice());
        order.setAppointmentTime(dto.getAppointmentTime());
        order.setContactPhone(dto.getContactPhone());
        order.setAddress(dto.getAddress());
        order.setRemark(dto.getRemark());
        order.setStatus(0); // 待派单

        this.save(order);
        
        sendOrderNotifyMessage(order.getId(), order.getOrderNo(), null, "create", "admin", "order", "新订单预约",
            String.format("有新的服务订单：订单编号 %s，服务项目：%s，预约时间：%s，联系人：%s",
                orderNo, serviceItem.getName(), dto.getAppointmentTime(), dto.getContactPhone()));
        
        return Result.success("预约成功，订单编号：" + orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> dispatchOrder(DispatchDTO dto) {
        ServiceOrder order = this.getById(dto.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("当前订单状态不允许派单");
        }
        order.setNurseId(dto.getNurseId());
        order.setNurseName(dto.getNurseName());
        order.setStatus(1); // 服务中
        this.updateById(order);
        
        if (order.getUserId() != null && order.getUserId() > 0) {
            sendOrderNotifyMessage(order.getId(), order.getOrderNo(), order.getUserId(), "dispatch", "user", "order", "订单已受理",
                String.format("您的订单 %s 已受理，服务人员：%s，服务时间：%s",
                    order.getOrderNo(), dto.getNurseName(), order.getAppointmentTime()));
        }
        
        return Result.success("派单成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> completeOrder(Long orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error("当前订单状态不允许完成");
        }
        order.setStatus(2); // 已完成
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);
        return Result.success("订单已完成");
    }

    @Override
    public Result<?> cancelOrder(Long orderId, String reason) {
        return cancelOrder(orderId, reason, "admin");
    }

    /**
     * 用户自主取消订单
     */
    public Result<?> userCancelOrder(Long orderId, Long userId, String reason) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 验证用户权限
        if (!userId.equals(order.getUserId())) {
            return Result.error("您无权取消此订单");
        }
        return cancelOrder(orderId, reason, "manual");
    }

    /**
     * 超时自动取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void timeoutCancelOrder(Long orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(3); // 已取消
            order.setCancelReason("订单超时未受理，自动取消");
            order.setCancelType("timeout");
            order.setCancelTime(LocalDateTime.now());
            this.updateById(order);
            
            if (order.getUserId() != null && order.getUserId() > 0) {
                sendOrderNotifyMessage(order.getId(), order.getOrderNo(), order.getUserId(), "timeout_cancel", "user", "order", "订单已取消",
                    String.format("您的订单 %s 因超时未受理已自动取消，请重新预约", order.getOrderNo()));
            }
        }
    }

    /**
     * 批量处理超时订单
     */
    public void processTimeoutOrders() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        log.info("开始查询超时订单，超时时间: {}", timeoutTime);
        
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getStatus, 0)
               .lt(ServiceOrder::getCreateTime, timeoutTime);
        
        java.util.List<ServiceOrder> timeoutOrders = this.list(wrapper);
        log.info("查询到超时订单数量: {}", timeoutOrders.size());
        
        for (ServiceOrder order : timeoutOrders) {
            try {
                timeoutCancelOrder(order.getId());
                log.info("超时订单取消成功，订单编号: {}", order.getOrderNo());
            } catch (Exception e) {
                log.error("超时订单取消失败，订单编号: {}, 错误: {}", order.getOrderNo(), e.getMessage(), e);
            }
        }
    }

    /**
     * 取消订单（通用方法）
     * @param orderId 订单ID
     * @param reason 取消原因
     * @param cancelType 取消类型：manual-用户自主取消，admin-管理员取消，timeout-超时自动取消
     */
    private Result<?> cancelOrder(Long orderId, String reason, String cancelType) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() >= 2) {
            return Result.error("当前订单状态不允许取消");
        }
        order.setStatus(3); // 已取消
        order.setCancelReason(reason);
        order.setCancelType(cancelType);
        order.setCancelTime(LocalDateTime.now());
        this.updateById(order);
        
        if ("manual".equals(cancelType) && order.getUserId() != null && order.getUserId() > 0) {
            sendOrderNotifyMessage(order.getId(), order.getOrderNo(), null, "manual_cancel", "admin", "order", "订单已取消",
                String.format("用户取消了订单：订单编号 %s，服务项目：%s", order.getOrderNo(), order.getServiceName()));
        } else if ("admin".equals(cancelType) && order.getUserId() != null && order.getUserId() > 0) {
            sendOrderNotifyMessage(order.getId(), order.getOrderNo(), order.getUserId(), "admin_cancel", "user", "order", "订单已取消",
                String.format("您的订单 %s 已被管理员取消，原因：%s", order.getOrderNo(), reason));
        }
        
        return Result.success("订单已取消");
    }

    @Override
    public ServiceOrder getDetail(Long orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order != null && order.getStatus() == 2) {
            Evaluation eval = evaluationMapper.selectOne(
                    new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, orderId));
            if (eval != null) {
                order.setEvalScore(eval.getScore());
                order.setEvalContent(eval.getContent());
            }
        }
        return order;
    }

    private void sendOrderNotifyMessage(Long orderId, String orderNo, Long userId, String eventType,
                                        String targetType, String type, String title, String content) {
        OrderNotifyDTO dto = OrderNotifyDTO.builder()
                .orderId(orderId)
                .orderNo(orderNo)
                .userId(userId)
                .eventType(eventType)
                .targetType(targetType)
                .type(type)
                .title(title)
                .content(content)
                .build();

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new org.springframework.transaction.support.TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            sendMQMessage(dto, orderId, eventType);
                        }
                    });
        } else {
            sendMQMessage(dto, orderId, eventType);
        }
    }

    private void sendMQMessage(OrderNotifyDTO dto, Long orderId, String eventType) {
        try {
            log.info("发送订单通知MQ消息: orderId={}, eventType={}", orderId, eventType);
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_NOTIFY_EXCHANGE,
                    "order.notify." + eventType, dto);
        } catch (Exception e) {
            log.warn("发送MQ消息失败，使用降级方案直接写入数据库: orderId={}, eventType={}, error={}", 
                    orderId, eventType, e.getMessage());
            fallbackToDirectDatabaseWrite(dto);
        }
    }

    private void fallbackToDirectDatabaseWrite(OrderNotifyDTO dto) {
        try {
            if ("admin".equals(dto.getTargetType())) {
                messageService.sendOrderMessageToAdmin(dto.getOrderId(), dto.getType(), dto.getTitle(), dto.getContent());
            } else if ("user".equals(dto.getTargetType())) {
                messageService.sendOrderMessageToUser(dto.getUserId(), dto.getOrderId(), dto.getType(), dto.getTitle(), dto.getContent());
            }
            log.info("降级方案执行成功，消息已直接写入数据库: orderId={}, eventType={}", dto.getOrderId(), dto.getEventType());
        } catch (Exception e) {
            log.error("降级方案执行失败，消息发送失败: orderId={}, eventType={}", dto.getOrderId(), dto.getEventType(), e);
        }
    }
}
