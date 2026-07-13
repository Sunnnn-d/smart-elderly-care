package com.smart.elderly.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求参数
 */
@Data
public class PageRequest implements Serializable {

    private int pageNum = 1;
    private int pageSize = 10;
    private String keyword;
}
