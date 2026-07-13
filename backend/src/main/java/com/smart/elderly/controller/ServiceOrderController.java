package com.smart.elderly.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.dto.AppointmentDTO;
import com.smart.elderly.dto.DispatchDTO;
import com.smart.elderly.dto.OrderQueryDTO;
import com.smart.elderly.entity.ServiceOrder;
import com.smart.elderly.service.ServiceOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 服务订单控制器
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;
    
    private final JwtUtil jwtUtil;

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<ServiceOrder>> list(OrderQueryDTO dto) {
        return serviceOrderService.pageList(dto);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        return Result.success(serviceOrderService.getDetail(id));
    }

    /**
     * 创建预约订单（前端用户预约）
     */
    @PostMapping("/appointment")
    public Result<?> createOrder(@Valid @RequestBody AppointmentDTO dto) {
        return serviceOrderService.createOrder(dto);
    }

    /**
     * 派单
     */
    @PutMapping("/dispatch")
    public Result<?> dispatch(@RequestBody DispatchDTO dto) {
        return serviceOrderService.dispatchOrder(dto);
    }

    /**
     * 完成订单
     */
    @PutMapping("/complete/{id}")
    public Result<?> complete(@PathVariable Long id) {
        return serviceOrderService.completeOrder(id);
    }

    /**
     * 取消订单（管理员）
     */
    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return serviceOrderService.cancelOrder(id, reason);
    }

    // ==================== 客户端用户订单接口 ====================

    /**
     * 获取客户端用户订单列表
     */
    @GetMapping("/app-user/list")
    public Result<PageResult<ServiceOrder>> getAppUserOrderList(HttpServletRequest request,
                                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                                               @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getUserId, userId)
               .orderByDesc(ServiceOrder::getCreateTime);
        
        Page<ServiceOrder> page = serviceOrderService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(PageResult.from(page));
    }

    /**
     * 获取客户端用户订单详情
     */
    @GetMapping("/app-user/{id}")
    public Result<?> getAppUserOrderDetail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        ServiceOrder order = serviceOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            return Result.error("您无权查看此订单");
        }
        return Result.success(serviceOrderService.getDetail(id));
    }

    /**
     * 用户自主取消订单
     */
    @PutMapping("/app-user/cancel/{id}")
    public Result<?> userCancel(HttpServletRequest request, @PathVariable Long id, 
                                @RequestParam(required = false) String reason) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return serviceOrderService.userCancelOrder(id, userId, reason);
    }
}
