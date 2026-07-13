package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.Result;
import com.smart.elderly.dto.EvaluationDTO;
import com.smart.elderly.entity.Evaluation;

public interface EvaluationService extends IService<Evaluation> {

    Result<?> addEvaluation(EvaluationDTO dto);

    Evaluation getByOrderId(Long orderId);
}
