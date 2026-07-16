package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Activity;
import com.smart.elderly.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/list")
    public Result<PageResult<Activity>> list(@RequestParam Map<String, Object> params) {
        return activityService.pageList(params);
    }

    @GetMapping("/all")
    public Result<?> getAll() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }

    @PostMapping
    public Result<?> add(@RequestBody Activity activity) {
        return activityService.addActivity(activity);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Activity activity) {
        return activityService.updateActivity(id, activity);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return activityService.deleteActivity(id);
    }

    @PutMapping("/publish/{id}")
    public Result<?> publish(@PathVariable Long id) {
        return activityService.publishActivity(id);
    }

    @PutMapping("/close/{id}")
    public Result<?> close(@PathVariable Long id) {
        return activityService.closeActivity(id);
    }
}