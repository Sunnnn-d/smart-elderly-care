package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.EmergencyCall;
import com.smart.elderly.mapper.EmergencyCallMapper;
import com.smart.elderly.service.EmergencyCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmergencyCallServiceImpl extends ServiceImpl<EmergencyCallMapper, EmergencyCall> implements EmergencyCallService {

    @Override
    public Result<PageResult<EmergencyCall>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<EmergencyCall> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("elderlyId")) {
            wrapper.eq(EmergencyCall::getElderlyId, params.get("elderlyId"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(EmergencyCall::getStatus, params.get("status"));
        }
        if (params.containsKey("callType") && StringUtils.hasText((String) params.get("callType"))) {
            wrapper.eq(EmergencyCall::getCallType, params.get("callType"));
        }
        wrapper.orderByDesc(EmergencyCall::getCreateTime);

        Page<EmergencyCall> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<EmergencyCall> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createCall(EmergencyCall call) {
        call.setCallTime(LocalDateTime.now());
        call.setStatus(0);
        this.save(call);
        return Result.success("紧急呼叫创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> handleCall(Long id, Long responderId, String responderName) {
        EmergencyCall call = this.getById(id);
        if (call == null) {
            return Result.error("紧急呼叫记录不存在");
        }
        if (call.getStatus() != 0) {
            return Result.error("当前状态不允许处理");
        }
        call.setStatus(1);
        call.setResponderId(responderId);
        call.setResponderName(responderName);
        call.setResponseTime(LocalDateTime.now());
        this.updateById(call);
        return Result.success("紧急呼叫已响应");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> completeCall(Long id, String handleResult) {
        EmergencyCall call = this.getById(id);
        if (call == null) {
            return Result.error("紧急呼叫记录不存在");
        }
        if (call.getStatus() != 1) {
            return Result.error("当前状态不允许完成");
        }
        call.setStatus(2);
        call.setHandleResult(handleResult);
        this.updateById(call);
        return Result.success("紧急呼叫已处理完成");
    }

    @Override
    public Result<?> getCallsByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<EmergencyCall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmergencyCall::getElderlyId, elderlyId)
               .orderByDesc(EmergencyCall::getCreateTime);
        List<EmergencyCall> calls = this.list(wrapper);
        return Result.success(calls);
    }
}