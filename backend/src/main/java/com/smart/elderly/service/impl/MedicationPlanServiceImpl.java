package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.MedicationPlan;
import com.smart.elderly.mapper.MedicationPlanMapper;
import com.smart.elderly.service.MedicationPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicationPlanServiceImpl extends ServiceImpl<MedicationPlanMapper, MedicationPlan> implements MedicationPlanService {

    @Override
    public Result<PageResult<MedicationPlan>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(String.valueOf(params.get("pageNum"))) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(String.valueOf(params.get("pageSize"))) : 10;
        
        LambdaQueryWrapper<MedicationPlan> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("elderlyId")) {
            wrapper.eq(MedicationPlan::getElderlyId, Long.parseLong(String.valueOf(params.get("elderlyId"))));
        }
        if (params.containsKey("elderlyName") && StringUtils.hasText((String) params.get("elderlyName"))) {
            wrapper.like(MedicationPlan::getElderlyName, params.get("elderlyName"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(MedicationPlan::getStatus, Integer.parseInt(String.valueOf(params.get("status"))));
        }
        if (params.containsKey("medicineName") && StringUtils.hasText((String) params.get("medicineName"))) {
            wrapper.like(MedicationPlan::getMedicineName, params.get("medicineName"));
        }
        wrapper.orderByDesc(MedicationPlan::getCreateTime);

        Page<MedicationPlan> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<MedicationPlan> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addPlan(MedicationPlan plan) {
        this.save(plan);
        return Result.success("用药计划添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updatePlan(Long id, MedicationPlan plan) {
        MedicationPlan existing = this.getById(id);
        if (existing == null) {
            return Result.error("用药计划不存在");
        }
        plan.setId(id);
        this.updateById(plan);
        return Result.success("用药计划更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deletePlan(Long id) {
        MedicationPlan plan = this.getById(id);
        if (plan == null) {
            return Result.error("用药计划不存在");
        }
        this.removeById(id);
        return Result.success("用药计划删除成功");
    }

    @Override
    public Result<?> getPlanById(Long id) {
        MedicationPlan plan = this.getById(id);
        if (plan == null) {
            return Result.error("用药计划不存在");
        }
        return Result.success(plan);
    }

    @Override
    public Result<?> getPlansByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<MedicationPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicationPlan::getElderlyId, elderlyId)
               .eq(MedicationPlan::getStatus, 1)
               .orderByDesc(MedicationPlan::getCreateTime);
        List<MedicationPlan> plans = this.list(wrapper);
        return Result.success(plans);
    }
}