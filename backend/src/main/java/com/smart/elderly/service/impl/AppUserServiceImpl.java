package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.LoginDTO;
import com.smart.elderly.dto.RegisterDTO;
import com.smart.elderly.entity.AppUser;
import com.smart.elderly.mapper.AppUserMapper;
import com.smart.elderly.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Result<?> register(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }

        AppUser existUser = this.getOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, dto.getUsername()));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setStatus(1);

        this.save(user);

        return Result.success("注册成功");
    }

    @Override
    public Result<?> login(LoginDTO dto) {
        AppUser user = this.getOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, dto.getUsername()));
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

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), 0);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", 0);

        return Result.success("登录成功", data);
    }

    @Override
    public Result<?> getUserInfo(Long userId) {
        AppUser user = this.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<?> changeUsername(Long userId, String newUsername) {
        AppUser user = this.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (user.getUsername().equals(newUsername)) {
            return Result.error("新用户名与当前用户名相同");
        }

        AppUser existUser = this.getOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, newUsername)
                .ne(AppUser::getId, userId));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        user.setUsername(newUsername);
        this.updateById(user);
        return Result.success("用户名修改成功");
    }

    @Override
    public Result<?> changePassword(Long userId, ChangePasswordDTO dto) {
        AppUser user = this.getById(userId);
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
        this.updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        AppUser user = this.getById(userId);
        if (user != null) {
            user.setAvatar(avatarUrl);
            this.updateById(user);
        }
    }
}
