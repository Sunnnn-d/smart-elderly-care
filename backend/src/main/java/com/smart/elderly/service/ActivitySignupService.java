package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ActivitySignup;

import java.util.Map;

public interface ActivitySignupService extends IService<ActivitySignup> {

    Result<PageResult<ActivitySignup>> pageList(Map<String, Object> params);

    Result<?> signup(Long activityId, Long elderlyId, String elderlyName);

    Result<?> cancelSignup(Long id);

    Result<?> approveSignup(Long id);

    Result<?> signIn(Long id);

    Result<?> getSignupsByActivityId(Long activityId);

    Result<?> getSignupsByElderlyId(Long elderlyId);
}