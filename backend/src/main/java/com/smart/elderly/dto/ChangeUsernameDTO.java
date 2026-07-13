package com.smart.elderly.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeUsernameDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;
}