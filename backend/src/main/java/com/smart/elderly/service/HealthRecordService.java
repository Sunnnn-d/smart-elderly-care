package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.HealthRecord;

public interface HealthRecordService extends IService<HealthRecord> {

    Result<PageResult<HealthRecord>> pageList(int pageNum, int pageSize, Long elderlyId);

    Result<?> addRecord(HealthRecord record);
}
