package com.smart.elderly.controller;

import com.smart.elderly.common.Result;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.LoginDTO;
import com.smart.elderly.dto.RegisterDTO;
import com.smart.elderly.entity.SysUser;
import com.smart.elderly.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        return sysUserService.register(dto);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return sysUserService.login(dto);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo(@RequestAttribute("userId") Long userId) {
        return sysUserService.getUserInfo(userId);
    }

    /**
     * 修改用户名
     */
    @PutMapping("/username")
    public Result<?> changeUsername(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> params) {
        String newUsername = params.get("username");
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        return sysUserService.changeUsername(userId, newUsername);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody ChangePasswordDTO dto) {
        return sysUserService.changePassword(userId, dto);
    }

    /**
     * 上传头像
     */
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public Result<?> uploadAvatar(@RequestAttribute("userId") Long userId, @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        try {
            File uploadDir = new File("uploads/avatars/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File dest = new File("uploads/avatars/" + filename);
            file.transferTo(dest);

            String avatarUrl = "/api/user/avatar/" + filename;
            sysUserService.updateAvatar(userId, avatarUrl);

            Map<String, Object> data = new HashMap<>();
            data.put("avatar", avatarUrl);
            return Result.success("头像上传成功", data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("头像上传失败");
        }
    }
}
