package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.CarePlan;
import com.smart.elderly.service.CarePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 护理计划控制器
 */
@RestController
@RequestMapping("/care-plan")
@RequiredArgsConstructor
public class CarePlanController {

    private final CarePlanService carePlanService;

    /**
     * 分页查询护理计划
     */
    @GetMapping("/list")
    public Result<PageResult<CarePlan>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long elderlyId) {
        return carePlanService.pageList(pageNum, pageSize, elderlyId);
    }

    /**
     * 添加护理计划
     */
    @PostMapping
    public Result<?> add(@RequestBody CarePlan plan) {
        return carePlanService.addPlan(plan);
    }

    /**
     * 更新护理计划
     */
    @PutMapping
    public Result<?> update(@RequestBody CarePlan plan) {
        return carePlanService.updatePlan(plan);
    }

    /**
     * 删除护理计划
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return carePlanService.deletePlan(id);
    }
}
