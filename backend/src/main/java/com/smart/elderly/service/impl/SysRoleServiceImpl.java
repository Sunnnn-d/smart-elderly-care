package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.SysRole;
import com.smart.elderly.mapper.SysRoleMapper;
import com.smart.elderly.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public Result<PageResult<SysRole>> pageList(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleCode, keyword);
        }
        wrapper.orderByAsc(SysRole::getSortOrder);

        Page<SysRole> page = new Page<>(pageNum, pageSize);
        Page<SysRole> resultPage = this.page(page, wrapper);
        PageResult<SysRole> pageResult = new PageResult<>(
                resultPage.getRecords(), resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
        return Result.success(pageResult);
    }

    @Override
    public Result<List<SysRole>> listAll() {
        log.info("Executing listAll method");
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSortOrder);
        List<SysRole> roles = this.list(wrapper);
        log.info("Found {} roles", roles.size());
        return Result.success(roles);
    }

    @Override
    public Result<?> addRole(SysRole role) {
        this.save(role);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> updateRole(SysRole role) {
        this.updateById(role);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteRole(Long id) {
        this.removeById(id);
        return Result.success("删除成功");
    }
}
