package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.EmergencyCall;

import java.util.Map;

public interface EmergencyCallService extends IService<EmergencyCall> {

    Result<PageResult<EmergencyCall>> pageList(Map<String, Object> params);

    Result<?> createCall(EmergencyCall call);

    Result<?> handleCall(Long id, Long responderId, String responderName);

    Result<?> completeCall(Long id, String handleResult);

    Result<?> getCallsByElderlyId(Long elderlyId);
}