package com.smart.elderly.dto;

import lombok.Data;

/**
 * 派单DTO
 */
@Data
public class DispatchDTO {

    private Long orderId;
    private Long nurseId;
    private String nurseName;
}
