package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.FileUploadConfig;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.LoginDTO;
import com.smart.elderly.dto.RegisterDTO;
import com.smart.elderly.dto.UserAddDTO;
import com.smart.elderly.dto.UserUpdateDTO;
import com.smart.elderly.entity.SysUser;
import com.smart.elderly.mapper.SysUserMapper;
import com.smart.elderly.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Result<?> register(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }

        SysUser existUser = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRole(2);
        user.setStatus(1);

        this.save(user);

        return Result.success("注册成功");
    }

    @Override
    public Result<?> login(LoginDTO dto) {
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());

        return Result.success("登录成功", data);
    }

    @Override
    public Result<?> getUserInfo(Long userId) {
        SysUser user = this.getById((Serializable) userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<?> listPage(int pageNum, int pageSize, String username, String realName, Integer role) {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery();
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (realName != null && !realName.isEmpty()) {
            wrapper.like(SysUser::getRealName, realName);
        }
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> resultPage = this.page(page, wrapper);
        
        resultPage.getRecords().forEach(u -> u.setPassword(null));
        
        return Result.success(new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getTotal(),
                pageNum,
                pageSize
        ));
    }

    @Override
    public Result<?> getById(Long id) {
        SysUser user = this.baseMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<?> add(UserAddDTO dto) {
        SysUser existUser = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setRole(dto.getRole() != null ? dto.getRole() : 2);
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        this.save(user);
        return Result.success("添加成功");
    }

    @Override
    public Result<?> update(Long id, UserUpdateDTO dto) {
        SysUser user = this.baseMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            SysUser existUser = this.getOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, dto.getUsername())
                    .ne(SysUser::getId, id));
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
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        this.baseMapper.updateById(user);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> delete(Long id) {
        SysUser user = this.baseMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getRole() == 1) {
            return Result.error("超级管理员不能删除");
        }
        this.baseMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> deleteBatch(Long[] ids) {
        for (Long id : ids) {
            SysUser user = this.baseMapper.selectById(id);
            if (user != null && user.getRole() == 1) {
                return Result.error("超级管理员不能删除");
            }
        }
        this.baseMapper.deleteBatchIds(java.util.Arrays.asList(ids));
        return Result.success("批量删除成功");
    }

    @Override
    public Result<?> resetPassword(Long id) {
        SysUser user = this.baseMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("a123456"));
        this.baseMapper.updateById(user);
        return Result.success("密码已重置为 a123456");
    }

    @Override
    public Result<?> uploadAvatar(Long id, MultipartFile file) {
        SysUser user = this.baseMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        try {
            File uploadDir = new File(FileUploadConfig.AVATAR_UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File dest = new File(FileUploadConfig.AVATAR_UPLOAD_DIR + filename);
            file.transferTo(dest);

            String avatarUrl = "/api/user/avatar/" + filename;
            user.setAvatar(avatarUrl);
            this.baseMapper.updateById(user);

            Map<String, Object> data = new HashMap<>();
            data.put("avatar", avatarUrl);
            return Result.success("头像上传成功", data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("头像上传失败");
        }
    }

    @Override
    public Result<?> changePassword(Long id, String oldPassword, String newPassword) {
        SysUser user = this.baseMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.baseMapper.updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public Result<?> changeUsername(Long userId, String newUsername) {
        SysUser user = this.baseMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (user.getUsername().equals(newUsername)) {
            return Result.error("新用户名与当前用户名相同");
        }

        SysUser existUser = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, newUsername)
                .ne(SysUser::getId, userId));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        user.setUsername(newUsername);
        this.baseMapper.updateById(user);
        return Result.success("用户名修改成功");
    }

    @Override
    public Result<?> changePassword(Long userId, ChangePasswordDTO dto) {
        SysUser user = this.baseMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return Result.error("旧密码不正确");
        }

        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            return Result.error("新密码不能与旧密码相同");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.baseMapper.updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        SysUser user = this.baseMapper.selectById(userId);
        if (user != null) {
            user.setAvatar(avatarUrl);
            this.baseMapper.updateById(user);
        }
    }
}
