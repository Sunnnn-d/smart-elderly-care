package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.entity.MedicationRecord;
import com.smart.elderly.entity.AppUser;
import com.smart.elderly.service.MedicationRecordService;
import com.smart.elderly.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/medication-record")
@RequiredArgsConstructor
@Slf4j
public class MedicationRecordController {

    private final MedicationRecordService medicationRecordService;
    
    private final AppUserService appUserService;
    
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<PageResult<MedicationRecord>> list(@RequestParam Map<String, Object> params) {
        return medicationRecordService.pageList(params);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return medicationRecordService.getRecordsByElderlyId(elderlyId);
    }

    @GetMapping("/user/list")
    public Result<?> getByUserId(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            log.info("MedicationRecordController.getByUserId - userId: {}", userId);
            if (userId == null) {
                log.warn("MedicationRecordController.getByUserId - userId is null");
                return Result.error("用户未登录");
            }
            AppUser user = appUserService.getById(userId);
            log.info("MedicationRecordController.getByUserId - user: {}", user);
            if (user == null) {
                log.warn("MedicationRecordController.getByUserId - user not found for userId: {}", userId);
                return Result.success(null);
            }
            Long elderlyId = user.getElderlyId();
            log.info("MedicationRecordController.getByUserId - elderlyId: {}", elderlyId);
            if (elderlyId == null) {
                log.warn("MedicationRecordController.getByUserId - elderlyId is null for user: {}", userId);
                return Result.success(null);
            }
            return medicationRecordService.getRecordsByElderlyId(elderlyId);
        } catch (Exception e) {
            log.error("MedicationRecordController.getByUserId error", e);
            throw e;
        }
    }

    @GetMapping("/plan/{planId}")
    public Result<?> getByPlanId(@PathVariable Long planId) {
        return medicationRecordService.getRecordsByPlanId(planId);
    }

    @PostMapping
    public Result<?> add(@RequestBody MedicationRecord record) {
        return medicationRecordService.addRecord(record);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MedicationRecord record) {
        return medicationRecordService.updateRecord(id, record);
    }
}