package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.entity.MedicationPlan;
import com.smart.elderly.entity.AppUser;
import com.smart.elderly.service.MedicationPlanService;
import com.smart.elderly.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/medication-plan")
@RequiredArgsConstructor
@Slf4j
public class MedicationPlanController {

    private final MedicationPlanService medicationPlanService;
    
    private final AppUserService appUserService;
    
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<PageResult<MedicationPlan>> list(@RequestParam Map<String, Object> params) {
        return medicationPlanService.pageList(params);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return medicationPlanService.getPlanById(id);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return medicationPlanService.getPlansByElderlyId(elderlyId);
    }

    @GetMapping("/user/list")
    public Result<?> getByUserId(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            log.info("MedicationPlanController.getByUserId - userId: {}", userId);
            if (userId == null) {
                log.warn("MedicationPlanController.getByUserId - userId is null");
                return Result.error("用户未登录");
            }
            AppUser user = appUserService.getById(userId);
            log.info("MedicationPlanController.getByUserId - user: {}", user);
            if (user == null) {
                log.warn("MedicationPlanController.getByUserId - user not found for userId: {}", userId);
                return Result.success(null);
            }
            Long elderlyId = user.getElderlyId();
            log.info("MedicationPlanController.getByUserId - elderlyId: {}", elderlyId);
            if (elderlyId == null) {
                log.warn("MedicationPlanController.getByUserId - elderlyId is null for user: {}", userId);
                return Result.success(null);
            }
            return medicationPlanService.getPlansByElderlyId(elderlyId);
        } catch (Exception e) {
            log.error("MedicationPlanController.getByUserId error", e);
            throw e;
        }
    }

    @PostMapping
    public Result<?> add(@RequestBody MedicationPlan plan) {
        return medicationPlanService.addPlan(plan);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MedicationPlan plan) {
        return medicationPlanService.updatePlan(id, plan);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return medicationPlanService.deletePlan(id);
    }
}