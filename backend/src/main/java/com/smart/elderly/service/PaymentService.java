package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Payment;

import java.util.Map;

public interface PaymentService extends IService<Payment> {

    Result<PageResult<Payment>> pageList(Map<String, Object> params);

    Result<?> createPayment(Payment payment);

    Result<?> updatePayment(Long id, Payment payment);

    Result<?> getPaymentById(Long id);

    Result<?> getPaymentsByElderlyId(Long elderlyId);

    Result<?> getPaymentsByBillId(Long billId);
}