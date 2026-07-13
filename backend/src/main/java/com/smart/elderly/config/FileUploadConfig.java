package com.smart.elderly.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * 文件上传配置类
 */
@Configuration
public class FileUploadConfig {

    /**
     * 头像上传目录
     */
    public static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";

    /**
     * 初始化上传目录
     */
    @PostConstruct
    public void init() {
        File dir = new File(AVATAR_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
