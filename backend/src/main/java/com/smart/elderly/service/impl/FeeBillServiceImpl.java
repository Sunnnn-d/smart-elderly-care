package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeBill;
import com.smart.elderly.mapper.FeeBillMapper;
import com.smart.elderly.service.FeeBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeeBillServiceImpl extends ServiceImpl<FeeBillMapper, FeeBill> implements FeeBillService {

    @Override
    public Result<PageResult<FeeBill>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<FeeBill> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("elderlyId")) {
            wrapper.eq(FeeBill::getElderlyId, params.get("elderlyId"));
        }
        if (params.containsKey("elderlyName") && StringUtils.hasText((String) params.get("elderlyName"))) {
            wrapper.like(FeeBill::getElderlyName, params.get("elderlyName"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(FeeBill::getStatus, params.get("status"));
        }
        if (params.containsKey("billMonth") && StringUtils.hasText((String) params.get("billMonth"))) {
            wrapper.eq(FeeBill::getBillMonth, params.get("billMonth"));
        }
        if (params.containsKey("billNo") && StringUtils.hasText((String) params.get("billNo"))) {
            wrapper.like(FeeBill::getBillNo, params.get("billNo"));
        }
        wrapper.orderByDesc(FeeBill::getCreateTime);

        Page<FeeBill> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<FeeBill> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addBill(FeeBill bill) {
        this.save(bill);
        return Result.success("费用账单添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateBill(Long id, FeeBill bill) {
        FeeBill existing = this.getById(id);
        if (existing == null) {
            return Result.error("费用账单不存在");
        }
        bill.setId(id);
        this.updateById(bill);
        return Result.success("费用账单更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteBill(Long id) {
        FeeBill bill = this.getById(id);
        if (bill == null) {
            return Result.error("费用账单不存在");
        }
        this.removeById(id);
        return Result.success("费用账单删除成功");
    }

    @Override
    public Result<?> getBillById(Long id) {
        FeeBill bill = this.getById(id);
        if (bill == null) {
            return Result.error("费用账单不存在");
        }
        return Result.success(bill);
    }

    @Override
    public Result<?> getBillsByElderlyId(Long elderlyId) {
        LambdaQueryWrapper<FeeBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeBill::getElderlyId, elderlyId)
               .orderByDesc(FeeBill::getBillMonth);
        List<FeeBill> bills = this.list(wrapper);
        return Result.success(bills);
    }

    @Override
    public Result<?> getBillByMonth(Long elderlyId, String month) {
        LambdaQueryWrapper<FeeBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeBill::getElderlyId, elderlyId)
               .eq(FeeBill::getBillMonth, month);
        FeeBill bill = this.getOne(wrapper);
        if (bill == null) {
            return Result.error("该月份账单不存在");
        }
        return Result.success(bill);
    }
}