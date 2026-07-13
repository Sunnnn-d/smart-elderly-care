package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.dto.AppointmentDTO;
import com.smart.elderly.dto.DispatchDTO;
import com.smart.elderly.dto.OrderQueryDTO;
import com.smart.elderly.entity.ServiceOrder;

public interface ServiceOrderService extends IService<ServiceOrder> {

    Result<PageResult<ServiceOrder>> pageList(OrderQueryDTO dto);

    Result<?> createOrder(AppointmentDTO dto);

    Result<?> dispatchOrder(DispatchDTO dto);

    Result<?> completeOrder(Long orderId);

    Result<?> cancelOrder(Long orderId, String reason);

    /**
     * 用户自主取消订单
     */
    Result<?> userCancelOrder(Long orderId, Long userId, String reason);

    /**
     * 批量处理超时订单（超过30分钟未派单自动取消）
     */
    void processTimeoutOrders();

    ServiceOrder getDetail(Long orderId);
}
