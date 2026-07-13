package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.HealthRecord;
import com.smart.elderly.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 健康监测控制器
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    /**
     * 分页查询健康记录
     */
    @GetMapping("/list")
    public Result<PageResult<HealthRecord>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long elderlyId) {
        return healthRecordService.pageList(pageNum, pageSize, elderlyId);
    }

    /**
     * 添加健康记录
     */
    @PostMapping
    public Result<?> add(@RequestBody HealthRecord record) {
        return healthRecordService.addRecord(record);
    }
}
