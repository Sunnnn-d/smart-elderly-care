package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeItem;
import com.smart.elderly.mapper.FeeItemMapper;
import com.smart.elderly.service.FeeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeeItemServiceImpl extends ServiceImpl<FeeItemMapper, FeeItem> implements FeeItemService {

    @Override
    public Result<PageResult<FeeItem>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<FeeItem> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("itemName") && StringUtils.hasText((String) params.get("itemName"))) {
            wrapper.like(FeeItem::getItemName, params.get("itemName"));
        }
        if (params.containsKey("feeType") && StringUtils.hasText((String) params.get("feeType"))) {
            wrapper.eq(FeeItem::getFeeType, params.get("feeType"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(FeeItem::getStatus, params.get("status"));
        }
        wrapper.orderByAsc(FeeItem::getSortOrder);

        Page<FeeItem> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<FeeItem> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addItem(FeeItem item) {
        this.save(item);
        return Result.success("费用项目添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateItem(Long id, FeeItem item) {
        FeeItem existing = this.getById(id);
        if (existing == null) {
            return Result.error("费用项目不存在");
        }
        item.setId(id);
        this.updateById(item);
        return Result.success("费用项目更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteItem(Long id) {
        FeeItem item = this.getById(id);
        if (item == null) {
            return Result.error("费用项目不存在");
        }
        this.removeById(id);
        return Result.success("费用项目删除成功");
    }

    @Override
    public Result<?> getItemById(Long id) {
        FeeItem item = this.getById(id);
        if (item == null) {
            return Result.error("费用项目不存在");
        }
        return Result.success(item);
    }

    @Override
    public Result<?> getAllItems() {
        LambdaQueryWrapper<FeeItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FeeItem::getStatus, 1)
               .orderByAsc(FeeItem::getSortOrder);
        List<FeeItem> items = this.list(wrapper);
        return Result.success(items);
    }
}