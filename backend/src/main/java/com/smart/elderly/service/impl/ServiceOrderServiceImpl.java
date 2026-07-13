package com.smart.elderly.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.dto.AppointmentDTO;
import com.smart.elderly.dto.DispatchDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {

    private final EvaluationMapper evaluationMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final MessageService messageService;

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
        
        // 发送消息通知管理员有新订单
        messageService.sendOrderMessageToAdmin(order.getId(), "order", "新订单预约", 
            String.format("有新的服务订单：订单编号 %s，服务项目：%s，预约时间：%s，联系人：%s", 
                orderNo, serviceItem.getName(), dto.getAppointmentTime(), dto.getContactPhone()));
        
        return Result.success("预约成功，订单编号：" + orderNo);
    }

    @Override
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
        
        // 如果有用户ID，发送消息通知用户订单已受理
        if (order.getUserId() != null && order.getUserId() > 0) {
            messageService.sendOrderMessageToUser(order.getUserId(), order.getId(), "order", "订单已受理", 
                String.format("您的订单 %s 已受理，服务人员：%s，服务时间：%s", 
                    order.getOrderNo(), dto.getNurseName(), order.getAppointmentTime()));
        }
        
        return Result.success("派单成功");
    }

    @Override
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
    public void timeoutCancelOrder(Long orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(3); // 已取消
            order.setCancelReason("订单超时未受理，自动取消");
            order.setCancelType("timeout");
            order.setCancelTime(LocalDateTime.now());
            this.updateById(order);
            
            // 发送消息通知用户订单已取消
            if (order.getUserId() != null && order.getUserId() > 0) {
                messageService.sendMessageToUser(order.getUserId(), "order", "订单已取消",
                    String.format("您的订单 %s 因超时未受理已自动取消，请重新预约", order.getOrderNo()));
            }
        }
    }

    /**
     * 批量处理超时订单
     */
    public void processTimeoutOrders() {
        // 查询创建时间超过30分钟且状态为待派单的订单
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(30);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getStatus, 0)
               .lt(ServiceOrder::getCreateTime, timeoutTime);
        
        this.list(wrapper).forEach(order -> {
            timeoutCancelOrder(order.getId());
        });
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
        
        // 发送消息通知相关方
        if ("manual".equals(cancelType) && order.getUserId() != null && order.getUserId() > 0) {
            // 用户自主取消，通知管理员
            messageService.sendOrderMessageToAdmin(order.getId(), "order", "订单已取消",
                String.format("用户取消了订单：订单编号 %s，服务项目：%s", order.getOrderNo(), order.getServiceName()));
        } else if ("admin".equals(cancelType) && order.getUserId() != null && order.getUserId() > 0) {
            // 管理员取消，通知用户
            messageService.sendMessageToUser(order.getUserId(), "order", "订单已取消",
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
}
