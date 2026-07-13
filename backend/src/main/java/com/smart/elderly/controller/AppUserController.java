package com.smart.elderly.controller;

import com.smart.elderly.common.Result;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.UserAddDTO;
import com.smart.elderly.dto.UserUpdateDTO;
import com.smart.elderly.entity.AppUser;
import com.smart.elderly.service.AppUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.elderly.common.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 客户端用户管理控制器（后台）
 */
@RestController
@RequestMapping("/app-user")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取客户端用户列表（不分页）
     */
    @GetMapping("/list")
    public Result<?> list() {
        List<AppUser> users = appUserService.list();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    /**
     * 分页查询客户端用户列表
     */
    @GetMapping("/page")
    public Result<?> listPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone) {
        
        LambdaQueryWrapper<AppUser> wrapper = Wrappers.lambdaQuery();
        if (username != null && !username.isEmpty()) {
            wrapper.like(AppUser::getUsername, username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(AppUser::getRealName, realName);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(AppUser::getPhone, phone);
        }
        wrapper.orderByDesc(AppUser::getCreateTime);

        Page<AppUser> page = new Page<>(pageNum, pageSize);
        Page<AppUser> resultPage = appUserService.page(page, wrapper);
        
        resultPage.getRecords().forEach(u -> u.setPassword(null));
        
        return Result.success(new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getTotal(),
                pageNum,
                pageSize
        ));
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        AppUser user = appUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<?> add(@Valid @RequestBody UserAddDTO dto) {
        AppUser existUser = appUserService.getOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, dto.getUsername()));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        appUserService.save(user);
        return Result.success("添加成功");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        AppUser user = appUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            AppUser existUser = appUserService.getOne(new LambdaQueryWrapper<AppUser>()
                    .eq(AppUser::getUsername, dto.getUsername())
                    .ne(AppUser::getId, id));
            if (existUser != null) {
                return Result.error("用户名已存在");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getAge() != null) {
            user.setAge(dto.getAge());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getEmergencyContact() != null) {
            user.setEmergencyContact(dto.getEmergencyContact());
        }
        if (dto.getEmergencyPhone() != null) {
            user.setEmergencyPhone(dto.getEmergencyPhone());
        }
        if (dto.getHealthStatus() != null) {
            user.setHealthStatus(dto.getHealthStatus());
        }

        appUserService.updateById(user);
        return Result.success("更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        AppUser user = appUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        appUserService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestBody Long[] ids) {
        appUserService.removeByIds(java.util.Arrays.asList(ids));
        return Result.success("批量删除成功");
    }

    /**
     * 重置密码（重置为 123456）
     */
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id) {
        AppUser user = appUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        appUserService.updateById(user);
        return Result.success("密码已重置为 123456");
    }

    /**
     * 上传头像
     */
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<?> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        AppUser user = appUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = java.util.UUID.randomUUID().toString() + extension;

        try {
            java.io.File uploadDir = new java.io.File("uploads/avatars/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            java.io.File dest = new java.io.File("uploads/avatars/" + filename);
            file.transferTo(dest);

            String avatarUrl = "/api/app-user/avatar/" + filename;
            user.setAvatar(avatarUrl);
            appUserService.updateById(user);

            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("avatar", avatarUrl);
            return Result.success("头像上传成功", data);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return Result.error("头像上传失败");
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public Result<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO dto) {
        AppUser user = appUserService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return Result.error("旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        appUserService.updateById(user);
        return Result.success("密码修改成功");
    }
}