package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeBill;
import com.smart.elderly.service.FeeBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fee-bill")
@RequiredArgsConstructor
public class FeeBillController {

    private final FeeBillService feeBillService;

    @GetMapping("/list")
    public Result<PageResult<FeeBill>> list(@RequestParam Map<String, Object> params) {
        return feeBillService.pageList(params);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return feeBillService.getBillById(id);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return feeBillService.getBillsByElderlyId(elderlyId);
    }

    @GetMapping("/elderly/{elderlyId}/month/{month}")
    public Result<?> getByMonth(@PathVariable Long elderlyId, @PathVariable String month) {
        return feeBillService.getBillByMonth(elderlyId, month);
    }

    @PostMapping
    public Result<?> add(@RequestBody FeeBill bill) {
        return feeBillService.addBill(bill);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FeeBill bill) {
        return feeBillService.updateBill(id, bill);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return feeBillService.deleteBill(id);
    }
}