package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.CarePlan;

public interface CarePlanService extends IService<CarePlan> {

    Result<PageResult<CarePlan>> pageList(int pageNum, int pageSize, Long elderlyId);

    Result<?> addPlan(CarePlan plan);

    Result<?> updatePlan(CarePlan plan);

    Result<?> deletePlan(Long id);
}
