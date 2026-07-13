package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.CarePlan;
import com.smart.elderly.mapper.CarePlanMapper;
import com.smart.elderly.service.CarePlanService;
import org.springframework.stereotype.Service;

@Service
public class CarePlanServiceImpl extends ServiceImpl<CarePlanMapper, CarePlan> implements CarePlanService {

    @Override
    public Result<PageResult<CarePlan>> pageList(int pageNum, int pageSize, Long elderlyId) {
        LambdaQueryWrapper<CarePlan> wrapper = new LambdaQueryWrapper<>();
        if (elderlyId != null) {
            wrapper.eq(CarePlan::getElderlyId, elderlyId);
        }
        wrapper.orderByDesc(CarePlan::getCreateTime);

        Page<CarePlan> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<CarePlan> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<?> addPlan(CarePlan plan) {
        this.save(plan);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> updatePlan(CarePlan plan) {
        this.updateById(plan);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deletePlan(Long id) {
        this.removeById(id);
        return Result.success("删除成功");
    }
}
