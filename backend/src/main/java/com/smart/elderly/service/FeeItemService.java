package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeItem;

import java.util.Map;

public interface FeeItemService extends IService<FeeItem> {

    Result<PageResult<FeeItem>> pageList(Map<String, Object> params);

    Result<?> addItem(FeeItem item);

    Result<?> updateItem(Long id, FeeItem item);

    Result<?> deleteItem(Long id);

    Result<?> getItemById(Long id);

    Result<?> getAllItems();
}