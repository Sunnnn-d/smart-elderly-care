package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.LoginDTO;
import com.smart.elderly.dto.RegisterDTO;
import com.smart.elderly.dto.UserAddDTO;
import com.smart.elderly.dto.UserUpdateDTO;
import com.smart.elderly.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 登录
     */
    Result<?> login(LoginDTO dto);

    /**
     * 注册
     */
    Result<?> register(RegisterDTO dto);

    /**
     * 获取用户信息
     */
    Result<?> getUserInfo(Long userId);

    /**
     * 分页查询用户列表
     */
    Result<?> listPage(int pageNum, int pageSize, String username, String realName, Integer role);

    /**
     * 根据ID查询用户
     */
    Result<?> getById(Long id);

    /**
     * 新增用户
     */
    Result<?> add(UserAddDTO dto);

    /**
     * 更新用户
     */
    Result<?> update(Long id, UserUpdateDTO dto);

    /**
     * 删除用户
     */
    Result<?> delete(Long id);

    /**
     * 批量删除用户
     */
    Result<?> deleteBatch(Long[] ids);

    /**
     * 重置密码
     */
    Result<?> resetPassword(Long id);

    /**
     * 上传头像
     */
    Result<?> uploadAvatar(Long id, org.springframework.web.multipart.MultipartFile file);

    /**
     * 修改密码
     */
    Result<?> changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 修改用户名
     */
    Result<?> changeUsername(Long userId, String newUsername);

    /**
     * 修改密码（新接口）
     */
    Result<?> changePassword(Long userId, ChangePasswordDTO dto);

    /**
     * 更新头像
     */
    void updateAvatar(Long userId, String avatarUrl);
}
