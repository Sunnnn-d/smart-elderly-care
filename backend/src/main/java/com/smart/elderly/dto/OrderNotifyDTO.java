package com.smart.elderly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotifyDTO {

    private Long orderId;

    private String orderNo;

    private Long userId;

    private String eventType;

    private String targetType;

    private String type;

    private String title;

    private String content;
}