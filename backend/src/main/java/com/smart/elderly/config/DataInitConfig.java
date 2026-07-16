package com.smart.elderly.config;

import com.smart.elderly.entity.SysRole;
import com.smart.elderly.service.SysRoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitConfig {

    private final SysRoleService sysRoleService;

    @PostConstruct
    public void init() {
        List<SysRole> existingRoles = sysRoleService.list();
        if (existingRoles.isEmpty()) {
            log.info("Initializing default roles...");
            
            SysRole superAdmin = new SysRole();
            superAdmin.setId(1L);
            superAdmin.setRoleName("超级管理员");
            superAdmin.setRoleCode("SUPER_ADMIN");
            superAdmin.setDescription("系统最高权限管理员");
            superAdmin.setStatus(1);
            superAdmin.setSortOrder(1);
            sysRoleService.save(superAdmin);

            SysRole admin = new SysRole();
            admin.setId(2L);
            admin.setRoleName("普通管理员");
            admin.setRoleCode("ADMIN");
            admin.setDescription("日常管理权限");
            admin.setStatus(1);
            admin.setSortOrder(2);
            sysRoleService.save(admin);

            SysRole nurse = new SysRole();
            nurse.setId(3L);
            nurse.setRoleName("护理员");
            nurse.setRoleCode("NURSE");
            nurse.setDescription("护理服务人员");
            nurse.setStatus(1);
            nurse.setSortOrder(3);
            sysRoleService.save(nurse);

            SysRole doctor = new SysRole();
            doctor.setId(4L);
            doctor.setRoleName("医生");
            doctor.setRoleCode("DOCTOR");
            doctor.setDescription("医疗服务人员");
            doctor.setStatus(1);
            doctor.setSortOrder(4);
            sysRoleService.save(doctor);

            SysRole nutritionist = new SysRole();
            nutritionist.setId(5L);
            nutritionist.setRoleName("营养师");
            nutritionist.setRoleCode("NUTRITIONIST");
            nutritionist.setDescription("营养指导人员");
            nutritionist.setStatus(1);
            nutritionist.setSortOrder(5);
            sysRoleService.save(nutritionist);

            SysRole therapist = new SysRole();
            therapist.setId(6L);
            therapist.setRoleName("康复师");
            therapist.setRoleCode("THERAPIST");
            therapist.setDescription("康复治疗人员");
            therapist.setStatus(1);
            therapist.setSortOrder(6);
            sysRoleService.save(therapist);

            SysRole cleaner = new SysRole();
            cleaner.setId(7L);
            cleaner.setRoleName("保洁员");
            cleaner.setRoleCode("CLEANER");
            cleaner.setDescription("环境卫生人员");
            cleaner.setStatus(1);
            cleaner.setSortOrder(7);
            sysRoleService.save(cleaner);

            log.info("Default roles initialized successfully");
        }
    }
}