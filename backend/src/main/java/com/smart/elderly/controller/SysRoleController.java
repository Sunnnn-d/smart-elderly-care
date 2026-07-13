package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.SysRole;
import com.smart.elderly.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/list")
    public Result<PageResult<SysRole>> pageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return sysRoleService.pageList(pageNum, pageSize, keyword);
    }

    @GetMapping("/all")
    public Result<List<SysRole>> listAll() {
        return sysRoleService.listAll();
    }

    @PostMapping("/add")
    public Result<?> addRole(@RequestBody SysRole role) {
        return sysRoleService.addRole(role);
    }

    @PutMapping("/update")
    public Result<?> updateRole(@RequestBody SysRole role) {
        return sysRoleService.updateRole(role);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        return sysRoleService.deleteRole(id);
    }
}