package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    Result<PageResult<SysRole>> pageList(int pageNum, int pageSize, String keyword);

    Result<List<SysRole>> listAll();

    Result<?> addRole(SysRole role);

    Result<?> updateRole(SysRole role);

    Result<?> deleteRole(Long id);
}