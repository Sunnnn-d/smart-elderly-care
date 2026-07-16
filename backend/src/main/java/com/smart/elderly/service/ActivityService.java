package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Activity;

import java.util.Map;

public interface ActivityService extends IService<Activity> {

    Result<PageResult<Activity>> pageList(Map<String, Object> params);

    Result<?> addActivity(Activity activity);

    Result<?> updateActivity(Long id, Activity activity);

    Result<?> deleteActivity(Long id);

    Result<?> getActivityById(Long id);

    Result<?> getAllActivities();

    Result<?> publishActivity(Long id);

    Result<?> closeActivity(Long id);
}