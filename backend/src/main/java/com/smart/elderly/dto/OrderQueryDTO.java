package com.smart.elderly.dto;

import lombok.Data;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {

    private int pageNum = 1;
    private int pageSize = 10;
    private String elderlyName;
    private Integer status;
    private String orderNo;
}
