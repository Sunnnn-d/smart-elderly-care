package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.Result;
import com.smart.elderly.dto.EvaluationDTO;
import com.smart.elderly.entity.Evaluation;
import com.smart.elderly.entity.ServiceOrder;
import com.smart.elderly.mapper.EvaluationMapper;
import com.smart.elderly.mapper.ServiceOrderMapper;
import com.smart.elderly.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    private final ServiceOrderMapper serviceOrderMapper;

    @Override
    public Result<?> addEvaluation(EvaluationDTO dto) {
        ServiceOrder order = serviceOrderMapper.selectById(dto.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error("只有已完成的订单才能评价");
        }

        // 检查是否已评价
        Evaluation existing = this.getOne(new LambdaQueryWrapper<Evaluation>()
                .eq(Evaluation::getOrderId, dto.getOrderId()));
        if (existing != null) {
            return Result.error("该订单已评价");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(dto.getOrderId());
        evaluation.setOrderNo(order.getOrderNo());
        evaluation.setElderlyId(order.getElderlyId());
        evaluation.setElderlyName(order.getElderlyName());
        evaluation.setNurseId(order.getNurseId());
        evaluation.setNurseName(order.getNurseName());
        evaluation.setScore(dto.getScore());
        evaluation.setContent(dto.getContent());

        this.save(evaluation);
        return Result.success("评价成功");
    }

    @Override
    public Evaluation getByOrderId(Long orderId) {
        return this.getOne(new LambdaQueryWrapper<Evaluation>().eq(Evaluation::getOrderId, orderId));
    }
}
