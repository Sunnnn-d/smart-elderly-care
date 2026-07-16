package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.MedicationRecord;

import java.util.Map;

public interface MedicationRecordService extends IService<MedicationRecord> {

    Result<PageResult<MedicationRecord>> pageList(Map<String, Object> params);

    Result<?> addRecord(MedicationRecord record);

    Result<?> updateRecord(Long id, MedicationRecord record);

    Result<?> getRecordsByElderlyId(Long elderlyId);

    Result<?> getRecordsByPlanId(Long planId);
}