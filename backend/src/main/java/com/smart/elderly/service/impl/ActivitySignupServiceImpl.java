package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Activity;
import com.smart.elderly.entity.ActivitySignup;
import com.smart.elderly.mapper.ActivityMapper;
import com.smart.elderly.mapper.ActivitySignupMapper;
import com.smart.elderly.service.ActivitySignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup> implements ActivitySignupService {

    private final ActivityMapper activityMapper;

    @Override
    public Result<PageResult<ActivitySignup>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? Integer.parseInt(String.valueOf(params.get("pageNum"))) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(String.valueOf(params.get("pageSize"))) : 10;
        
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("activityId")) {
            wrapper.eq(ActivitySignup::getActivityId, Long.parseLong(String.valueOf(params.get("activityId"))));
        }
        if (params.containsKey("elderlyId")) {
            wrapper.eq(ActivitySignup::getElderlyId, Long.parseLong(String.valueOf(params.get("elderlyId"))));
        }
        if (params.containsKey("status")) {
            wrapper.eq(ActivitySignup::getStatus, Integer.parseInt(String.valueOf(params.get("status"))));
        }
        wrapper.orderByDesc(ActivitySignup::getSignupTime);

        Page<ActivitySignup> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<ActivitySignup> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> signup(Long activityId, Long elderlyId, String elderlyName) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        if (activity.getStatus() != 1) {
            return Result.error("活动当前不接受报名");
        }
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return Result.error("活动已满员");
        }

        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
               .eq(ActivitySignup::getElderlyId, elderlyId);
        if (this.count(wrapper) > 0) {
            return Result.error("您已报名此活动");
        }

        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setActivityName(activity.getActivityName());
        signup.setElderlyId(elderlyId);
        signup.setElderlyName(elderlyName);
        signup.setSignupTime(LocalDateTime.now());
        signup.setStatus(0);
        this.save(signup);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityMapper.updateById(activity);

        return Result.success("报名成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> cancelSignup(Long id) {
        ActivitySignup signup = this.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }
        if (signup.getStatus() == 2) {
            return Result.error("已签到，无法取消");
        }
        signup.setStatus(3);
        this.updateById(signup);

        Activity activity = activityMapper.selectById(signup.getActivityId());
        if (activity != null && activity.getCurrentParticipants() > 0) {
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityMapper.updateById(activity);
        }

        return Result.success("取消报名成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> approveSignup(Long id) {
        ActivitySignup signup = this.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }
        if (signup.getStatus() != 0) {
            return Result.error("当前状态不允许审核");
        }
        signup.setStatus(1);
        this.updateById(signup);
        return Result.success("审核通过");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> signIn(Long id) {
        ActivitySignup signup = this.getById(id);
        if (signup == null) {
            return Result.error("报名记录不存在");
        }
        if (signup.getStatus() != 1) {
            return Result.error("当前状态不允许签到");
        }
        signup.setStatus(2);
        signup.setSignInTime(LocalDateTime.now());
        this.updateById(signup);
        return Result.success("签到成功");
    }

    @Override
    public Result<?> getSignupsByActivityId(Long activityId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
               .orderByDesc(ActivitySignup::getSignupTime);
        List<ActivitySignup> signups = this.list(wrapper);
        return Result.success(signups);
    }

    @Override
    public Result<?> getSignupsByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getElderlyId, elderlyId)
               .orderByDesc(ActivitySignup::getSignupTime);
        List<ActivitySignup> signups = this.list(wrapper);
        return Result.success(signups);
    }
}