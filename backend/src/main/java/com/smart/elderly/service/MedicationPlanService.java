package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.MedicationPlan;

import java.util.Map;

public interface MedicationPlanService extends IService<MedicationPlan> {

    Result<PageResult<MedicationPlan>> pageList(Map<String, Object> params);

    Result<?> addPlan(MedicationPlan plan);

    Result<?> updatePlan(Long id, MedicationPlan plan);

    Result<?> deletePlan(Long id);

    Result<?> getPlanById(Long id);

    Result<?> getPlansByElderlyId(Long elderlyId);
}