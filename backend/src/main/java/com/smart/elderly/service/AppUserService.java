package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.Result;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.LoginDTO;
import com.smart.elderly.dto.RegisterDTO;
import com.smart.elderly.entity.AppUser;

public interface AppUserService extends IService<AppUser> {

    /**
     * 注册
     */
    Result<?> register(RegisterDTO dto);

    /**
     * 登录
     */
    Result<?> login(LoginDTO dto);

    /**
     * 获取用户信息
     */
    Result<?> getUserInfo(Long userId);

    /**
     * 修改用户名
     */
    Result<?> changeUsername(Long userId, String newUsername);

    /**
     * 修改密码
     */
    Result<?> changePassword(Long userId, ChangePasswordDTO dto);

    /**
     * 更新头像
     */
    void updateAvatar(Long userId, String avatarUrl);
}
