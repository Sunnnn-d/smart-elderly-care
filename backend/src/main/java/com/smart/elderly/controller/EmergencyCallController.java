package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.EmergencyCall;
import com.smart.elderly.service.EmergencyCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/emergency-call")
@RequiredArgsConstructor
public class EmergencyCallController {

    private final EmergencyCallService emergencyCallService;

    @GetMapping("/list")
    public Result<PageResult<EmergencyCall>> list(@RequestParam Map<String, Object> params) {
        return emergencyCallService.pageList(params);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return emergencyCallService.getCallsByElderlyId(elderlyId);
    }

    @PostMapping
    public Result<?> create(@RequestBody EmergencyCall call) {
        return emergencyCallService.createCall(call);
    }

    @PutMapping("/handle/{id}")
    public Result<?> handle(@PathVariable Long id, 
                            @RequestParam Long responderId, 
                            @RequestParam String responderName) {
        return emergencyCallService.handleCall(id, responderId, responderName);
    }

    @PutMapping("/complete/{id}")
    public Result<?> complete(@PathVariable Long id, @RequestParam String handleResult) {
        return emergencyCallService.completeCall(id, handleResult);
    }
}