package com.smart.elderly.controller;

import com.smart.elderly.common.Result;
import com.smart.elderly.dto.EvaluationDTO;
import com.smart.elderly.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * 添加评价
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody EvaluationDTO dto) {
        return evaluationService.addEvaluation(dto);
    }

    /**
     * 根据订单ID查询评价
     */
    @GetMapping("/order/{orderId}")
    public Result<?> getByOrderId(@PathVariable Long orderId) {
        return Result.success(evaluationService.getByOrderId(orderId));
    }
}
