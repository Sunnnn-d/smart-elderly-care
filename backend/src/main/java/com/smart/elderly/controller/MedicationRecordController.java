package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.MedicationRecord;
import com.smart.elderly.service.MedicationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/medication-record")
@RequiredArgsConstructor
public class MedicationRecordController {

    private final MedicationRecordService medicationRecordService;

    @GetMapping("/list")
    public Result<PageResult<MedicationRecord>> list(@RequestParam Map<String, Object> params) {
        return medicationRecordService.pageList(params);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return medicationRecordService.getRecordsByElderlyId(elderlyId);
    }

    @GetMapping("/plan/{planId}")
    public Result<?> getByPlanId(@PathVariable Long planId) {
        return medicationRecordService.getRecordsByPlanId(planId);
    }

    @PostMapping
    public Result<?> add(@RequestBody MedicationRecord record) {
        return medicationRecordService.addRecord(record);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MedicationRecord record) {
        return medicationRecordService.updateRecord(id, record);
    }
}