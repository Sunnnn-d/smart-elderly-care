package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ServiceItem;

public interface ServiceItemService extends IService<ServiceItem> {

    Result<PageResult<ServiceItem>> pageList(int pageNum, int pageSize, String keyword, String category);

    Result<?> addServiceItem(ServiceItem item);

    Result<?> updateServiceItem(ServiceItem item);

    Result<?> deleteServiceItem(Long id);
}
