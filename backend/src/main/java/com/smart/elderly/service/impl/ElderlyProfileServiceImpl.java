package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ElderlyProfile;
import com.smart.elderly.mapper.ElderlyProfileMapper;
import com.smart.elderly.service.ElderlyProfileService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ElderlyProfileServiceImpl extends ServiceImpl<ElderlyProfileMapper, ElderlyProfile> implements ElderlyProfileService {

    @Override
    public Result<PageResult<ElderlyProfile>> pageList(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<ElderlyProfile> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ElderlyProfile::getName, keyword)
                    .or().like(ElderlyProfile::getPhone, keyword);
        }
        wrapper.orderByDesc(ElderlyProfile::getCreateTime);

        Page<ElderlyProfile> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<ElderlyProfile> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<?> addElderly(ElderlyProfile profile) {
        this.save(profile);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> updateElderly(ElderlyProfile profile) {
        this.updateById(profile);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteElderly(Long id) {
        this.removeById(id);
        return Result.success("删除成功");
    }
}
