package com.smart.elderly.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeBill;
import com.smart.elderly.entity.Payment;
import com.smart.elderly.mapper.FeeBillMapper;
import com.smart.elderly.mapper.PaymentMapper;
import com.smart.elderly.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final FeeBillMapper feeBillMapper;

    @Override
    public Result<PageResult<Payment>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(String.valueOf(params.get("pageNum"))) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(String.valueOf(params.get("pageSize"))) : 10;
        
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("elderlyId")) {
            wrapper.eq(Payment::getElderlyId, Long.parseLong(String.valueOf(params.get("elderlyId"))));
        }
        if (params.containsKey("elderlyName") && StringUtils.hasText((String) params.get("elderlyName"))) {
            wrapper.like(Payment::getElderlyName, params.get("elderlyName"));
        }
        if (params.containsKey("billId")) {
            wrapper.eq(Payment::getBillId, Long.parseLong(String.valueOf(params.get("billId"))));
        }
        if (params.containsKey("status")) {
            wrapper.eq(Payment::getStatus, Integer.parseInt(String.valueOf(params.get("status"))));
        }
        if (params.containsKey("payMethod") && StringUtils.hasText((String) params.get("payMethod"))) {
            wrapper.eq(Payment::getPayMethod, params.get("payMethod"));
        }
        wrapper.orderByDesc(Payment::getCreateTime);

        Page<Payment> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<Payment> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createPayment(Payment payment) {
        String payNo = "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%03d", IdUtil.getSnowflakeNextId() % 1000);
        payment.setPayNo(payNo);
        
        if (payment.getStatus() == 2) {
            payment.setPayTime(LocalDateTime.now());
            
            if (payment.getBillId() != null) {
                FeeBill bill = feeBillMapper.selectById(payment.getBillId());
                if (bill != null) {
                    BigDecimal newPaidAmount = bill.getPaidAmount().add(payment.getPayAmount());
                    bill.setPaidAmount(newPaidAmount);
                    
                    if (newPaidAmount.compareTo(bill.getTotalAmount()) >= 0) {
                        bill.setStatus(2);
                    } else {
                        bill.setStatus(1);
                    }
                    feeBillMapper.updateById(bill);
                }
            }
        }
        
        this.save(payment);
        return Result.success("支付记录创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updatePayment(Long id, Payment payment) {
        Payment existing = this.getById(id);
        if (existing == null) {
            return Result.error("支付记录不存在");
        }
        payment.setId(id);
        this.updateById(payment);
        return Result.success("支付记录更新成功");
    }

    @Override
    public Result<?> getPaymentById(Long id) {
        Payment payment = this.getById(id);
        if (payment == null) {
            return Result.error("支付记录不存在");
        }
        return Result.success(payment);
    }

    @Override
    public Result<?> getPaymentsByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getElderlyId, elderlyId)
               .orderByDesc(Payment::getCreateTime);
        List<Payment> payments = this.list(wrapper);
        return Result.success(payments);
    }

    @Override
    public Result<?> getPaymentsByBillId(Long billId) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getBillId, billId)
               .orderByDesc(Payment::getCreateTime);
        List<Payment> payments = this.list(wrapper);
        return Result.success(payments);
    }
}