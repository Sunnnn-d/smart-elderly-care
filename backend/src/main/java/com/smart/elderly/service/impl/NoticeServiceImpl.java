package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Notice;
import com.smart.elderly.mapper.NoticeMapper;
import com.smart.elderly.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public Result<PageResult<Notice>> pageList(int pageNum, int pageSize, Integer type) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(Notice::getType, type);
        }
        wrapper.eq(Notice::getStatus, 1); // 只查询已发布的
        wrapper.orderByDesc(Notice::getIsTop).orderByDesc(Notice::getPublishTime);

        Page<Notice> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<Notice> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<?> addNotice(Notice notice) {
        this.save(notice);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> updateNotice(Notice notice) {
        this.updateById(notice);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteNotice(Long id) {
        this.removeById(id);
        return Result.success("删除成功");
    }
}
