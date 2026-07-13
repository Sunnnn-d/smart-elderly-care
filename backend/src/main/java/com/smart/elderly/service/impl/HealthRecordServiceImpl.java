package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.HealthRecord;
import com.smart.elderly.mapper.HealthRecordMapper;
import com.smart.elderly.service.HealthRecordService;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    public Result<PageResult<HealthRecord>> pageList(int pageNum, int pageSize, Long elderlyId) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        if (elderlyId != null) {
            wrapper.eq(HealthRecord::getElderlyId, elderlyId);
        }
        wrapper.orderByDesc(HealthRecord::getRecordTime);

        Page<HealthRecord> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<HealthRecord> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<?> addRecord(HealthRecord record) {
        this.save(record);
        return Result.success("添加成功");
    }
}
