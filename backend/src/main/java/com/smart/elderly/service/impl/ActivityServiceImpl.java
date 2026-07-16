package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Activity;
import com.smart.elderly.mapper.ActivityMapper;
import com.smart.elderly.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    public Result<PageResult<Activity>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("activityName") && StringUtils.hasText((String) params.get("activityName"))) {
            wrapper.like(Activity::getActivityName, params.get("activityName"));
        }
        if (params.containsKey("activityType") && StringUtils.hasText((String) params.get("activityType"))) {
            wrapper.eq(Activity::getActivityType, params.get("activityType"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(Activity::getStatus, params.get("status"));
        }
        wrapper.orderByDesc(Activity::getCreateTime);

        Page<Activity> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<Activity> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addActivity(Activity activity) {
        activity.setCurrentParticipants(0);
        this.save(activity);
        return Result.success("活动添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateActivity(Long id, Activity activity) {
        Activity existing = this.getById(id);
        if (existing == null) {
            return Result.error("活动不存在");
        }
        activity.setId(id);
        this.updateById(activity);
        return Result.success("活动更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteActivity(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        this.removeById(id);
        return Result.success("活动删除成功");
    }

    @Override
    public Result<?> getActivityById(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }

    @Override
    public Result<?> getAllActivities() {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1)
               .orderByDesc(Activity::getStartTime);
        List<Activity> activities = this.list(wrapper);
        return Result.success(activities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> publishActivity(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (activity.getStatus() != 0) {
            return Result.error("当前状态不允许发布");
        }
        activity.setStatus(1);
        this.updateById(activity);
        return Result.success("活动已发布");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> closeActivity(Long id) {
        Activity activity = this.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        activity.setStatus(3);
        this.updateById(activity);
        return Result.success("活动已结束");
    }
}