package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.MedicationRecord;
import com.smart.elderly.mapper.MedicationRecordMapper;
import com.smart.elderly.service.MedicationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicationRecordServiceImpl extends ServiceImpl<MedicationRecordMapper, MedicationRecord> implements MedicationRecordService {

    @Override
    public Result<PageResult<MedicationRecord>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<MedicationRecord> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("elderlyId")) {
            wrapper.eq(MedicationRecord::getElderlyId, params.get("elderlyId"));
        }
        if (params.containsKey("planId")) {
            wrapper.eq(MedicationRecord::getPlanId, params.get("planId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(MedicationRecord::getStatus, params.get("status"));
        }
        if (params.containsKey("medicineName") && StringUtils.hasText((String) params.get("medicineName"))) {
            wrapper.like(MedicationRecord::getMedicineName, params.get("medicineName"));
        }
        wrapper.orderByDesc(MedicationRecord::getCreateTime);

        Page<MedicationRecord> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<MedicationRecord> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRecord(MedicationRecord record) {
        this.save(record);
        return Result.success("用药记录添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateRecord(Long id, MedicationRecord record) {
        MedicationRecord existing = this.getById(id);
        if (existing == null) {
            return Result.error("用药记录不存在");
        }
        record.setId(id);
        this.updateById(record);
        return Result.success("用药记录更新成功");
    }

    @Override
    public Result<?> getRecordsByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<MedicationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicationRecord::getElderlyId, elderlyId)
               .orderByDesc(MedicationRecord::getCreateTime);
        List<MedicationRecord> records = this.list(wrapper);
        return Result.success(records);
    }

    @Override
    public Result<?> getRecordsByPlanId(Long planId) {
        LambdaQueryWrapper<MedicationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicationRecord::getPlanId, planId)
               .orderByDesc(MedicationRecord::getCreateTime);
        List<MedicationRecord> records = this.list(wrapper);
        return Result.success(records);
    }
}