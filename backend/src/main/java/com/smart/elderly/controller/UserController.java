package com.smart.elderly.controller;

import com.smart.elderly.common.Result;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.UserAddDTO;
import com.smart.elderly.dto.UserUpdateDTO;
import com.smart.elderly.entity.SysUser;
import com.smart.elderly.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理控制器（后台）
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService sysUserService;

    /**
     * 获取护理员列表（派单用）
     */
    @GetMapping("/nurses")
    public Result<?> listNurses() {
        List<SysUser> nurses = sysUserService.lambdaQuery()
                .eq(SysUser::getRole, 3)
                .eq(SysUser::getStatus, 1)
                .list();
        nurses.forEach(n -> n.setPassword(null));
        return Result.success(nurses);
    }

    /**
     * 获取用户列表（不分页）
     */
    @GetMapping("/list")
    public Result<?> list() {
        List<SysUser> users = sysUserService.list();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param username 用户名（模糊查询）
     * @param realName 真实姓名（模糊查询）
     * @param role 角色：1-超级管理员 2-普通管理员 3-护理员
     */
    @GetMapping("/page")
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer role) {
        return sysUserService.listPage(pageNum, pageSize, username, realName, role);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return sysUserService.getById(id);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody UserAddDTO dto) {
        return sysUserService.add(dto);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return sysUserService.update(id, dto);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return sysUserService.delete(id);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestBody Long[] ids) {
        return sysUserService.deleteBatch(ids);
    }

    /**
     * 重置密码（重置为 123456）
     */
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id) {
        return sysUserService.resetPassword(id);
    }

    /**
     * 上传头像
     */
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<?> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return sysUserService.uploadAvatar(id, file);
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public Result<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO dto) {
        return sysUserService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
    }
}
