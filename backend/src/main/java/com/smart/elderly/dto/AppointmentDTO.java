package com.smart.elderly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约服务DTO
 */
@Data
public class AppointmentDTO {

    /**
     * 客户端用户ID
     */
    private Long userId;

    @NotBlank(message = "老人姓名不能为空")
    private String elderlyName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    @NotNull(message = "服务项目ID不能为空")
    private Long serviceId;

    @NotNull(message = "预约时间不能为空")
    private LocalDateTime appointmentTime;

    private String address;
    private String remark;
}
