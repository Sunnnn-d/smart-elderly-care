package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Payment;
import com.smart.elderly.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/list")
    public Result<PageResult<Payment>> list(@RequestParam Map<String, Object> params) {
        return paymentService.pageList(params);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return paymentService.getPaymentsByElderlyId(elderlyId);
    }

    @GetMapping("/bill/{billId}")
    public Result<?> getByBillId(@PathVariable Long billId) {
        return paymentService.getPaymentsByBillId(billId);
    }

    @PostMapping
    public Result<?> create(@RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Payment payment) {
        return paymentService.updatePayment(id, payment);
    }
}