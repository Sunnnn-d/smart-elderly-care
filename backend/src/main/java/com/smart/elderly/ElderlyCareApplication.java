package com.smart.elderly;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@MapperScan("com.smart.elderly.mapper")
@EnableMethodSecurity
@EnableScheduling
public class ElderlyCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareApplication.class, args);
        System.out.println("========================================");
        System.out.println("   智慧养老系统后端服务启动成功！");
//        System.out.println("   接口文档地址: http://localhost:8088/api");
        System.out.println("========================================");
    }
}
