package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ServiceItem;
import com.smart.elderly.mapper.ServiceItemMapper;
import com.smart.elderly.service.ServiceItemService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {

    @Override
    public Result<PageResult<ServiceItem>> pageList(int pageNum, int pageSize, String keyword, String category) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ServiceItem::getName, keyword);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(ServiceItem::getCategory, category);
        }
        wrapper.orderByAsc(ServiceItem::getSortOrder).orderByDesc(ServiceItem::getCreateTime);

        Page<ServiceItem> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<ServiceItem> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<?> addServiceItem(ServiceItem item) {
        this.save(item);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> updateServiceItem(ServiceItem item) {
        this.updateById(item);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteServiceItem(Long id) {
        this.removeById(id);
        return Result.success("删除成功");
    }
}
