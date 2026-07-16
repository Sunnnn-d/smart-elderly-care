package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ActivitySignup;
import com.smart.elderly.service.ActivitySignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/activity-signup")
@RequiredArgsConstructor
public class ActivitySignupController {

    private final ActivitySignupService activitySignupService;

    @GetMapping("/list")
    public Result<PageResult<ActivitySignup>> list(@RequestParam Map<String, Object> params) {
        return activitySignupService.pageList(params);
    }

    @GetMapping("/activity/{activityId}")
    public Result<?> getByActivityId(@PathVariable Long activityId) {
        return activitySignupService.getSignupsByActivityId(activityId);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return activitySignupService.getSignupsByElderlyId(elderlyId);
    }

    @PostMapping
    public Result<?> signup(@RequestParam Long activityId, 
                           @RequestParam Long elderlyId, 
                           @RequestParam String elderlyName) {
        return activitySignupService.signup(activityId, elderlyId, elderlyName);
    }

    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id) {
        return activitySignupService.approveSignup(id);
    }

    @PutMapping("/sign-in/{id}")
    public Result<?> signIn(@PathVariable Long id) {
        return activitySignupService.signIn(id);
    }

    @PutMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        return activitySignupService.cancelSignup(id);
    }
}