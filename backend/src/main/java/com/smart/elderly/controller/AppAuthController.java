package com.smart.elderly.controller;

import com.smart.elderly.common.Result;
import com.smart.elderly.dto.ChangePasswordDTO;
import com.smart.elderly.dto.ChangeUsernameDTO;
import com.smart.elderly.dto.LoginDTO;
import com.smart.elderly.dto.RegisterDTO;
import com.smart.elderly.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/app-auth")
@RequiredArgsConstructor
@Slf4j
public class AppAuthController {

    private final AppUserService appUserService;

    @Value("${app.upload.avatar-path}")
    private String avatarUploadPath;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        return appUserService.register(dto);
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return appUserService.login(dto);
    }

    @GetMapping("/info")
    public Result<?> getUserInfo(@RequestAttribute("userId") Long userId) {
        return appUserService.getUserInfo(userId);
    }

    @PutMapping("/username")
    public Result<?> changeUsername(@RequestAttribute("userId") Long userId, @Valid @RequestBody ChangeUsernameDTO dto) {
        return appUserService.changeUsername(userId, dto.getUsername());
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestAttribute("userId") Long userId, @Valid @RequestBody ChangePasswordDTO dto) {
        return appUserService.changePassword(userId, dto);
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public Result<?> uploadAvatar(@RequestAttribute("userId") Long userId, @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名不能为空");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            return Result.error("无效的文件格式");
        }

        String extension = originalFilename.substring(dotIndex).toLowerCase();
        Set<String> allowedExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp");
        if (!allowedExtensions.contains(extension)) {
            return Result.error("只允许上传jpg、jpeg、png、gif、bmp格式的图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("文件类型不正确，请上传图片");
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return Result.error("图片大小不能超过5MB");
        }

        String filename = UUID.randomUUID().toString() + extension;

        try {
            File uploadDir = new File(avatarUploadPath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                return Result.error("无法创建上传目录");
            }

            File dest = new File(avatarUploadPath + filename);
            file.transferTo(dest);

            String avatarUrl = "/uploads/avatars/" + filename;
            appUserService.updateAvatar(userId, avatarUrl);

            Map<String, Object> data = new HashMap<>();
            data.put("avatar", avatarUrl);
            return Result.success("头像上传成功", data);
        } catch (IOException e) {
            log.error("头像上传失败, userId: {}", userId, e);
            return Result.error("头像上传失败");
        }
    }
}