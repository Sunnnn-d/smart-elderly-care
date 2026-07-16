package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.MedicationPlan;
import com.smart.elderly.service.MedicationPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/medication-plan")
@RequiredArgsConstructor
public class MedicationPlanController {

    private final MedicationPlanService medicationPlanService;

    @GetMapping("/list")
    public Result<PageResult<MedicationPlan>> list(@RequestParam Map<String, Object> params) {
        return medicationPlanService.pageList(params);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return medicationPlanService.getPlanById(id);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return medicationPlanService.getPlansByElderlyId(elderlyId);
    }

    @PostMapping
    public Result<?> add(@RequestBody MedicationPlan plan) {
        return medicationPlanService.addPlan(plan);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MedicationPlan plan) {
        return medicationPlanService.updatePlan(id, plan);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return medicationPlanService.deletePlan(id);
    }
}