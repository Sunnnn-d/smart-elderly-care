package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeBill;

import java.util.Map;

public interface FeeBillService extends IService<FeeBill> {

    Result<PageResult<FeeBill>> pageList(Map<String, Object> params);

    Result<?> addBill(FeeBill bill);

    Result<?> updateBill(Long id, FeeBill bill);

    Result<?> deleteBill(Long id);

    Result<?> getBillById(Long id);

    Result<?> getBillsByElderlyId(Long elderlyId);

    Result<?> getBillByMonth(Long elderlyId, String month);
}