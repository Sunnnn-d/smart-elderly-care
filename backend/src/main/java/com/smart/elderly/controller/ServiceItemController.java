package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ServiceItem;
import com.smart.elderly.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 服务项目管理控制器
 */
@RestController
@RequestMapping("/service-item")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    /**
     * 分页查询服务项目
     */
    @GetMapping("/list")
    public Result<PageResult<ServiceItem>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return serviceItemService.pageList(pageNum, pageSize, keyword, category);
    }

    /**
     * 查询所有上架服务（前端展示用）
     */
    @GetMapping("/all")
    public Result<?> listAll() {
        return Result.success(serviceItemService.list());
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(serviceItemService.getById(id));
    }

    /**
     * 添加服务项目
     */
    @PostMapping
    public Result<?> add(@RequestBody ServiceItem item) {
        return serviceItemService.addServiceItem(item);
    }

    /**
     * 更新服务项目
     */
    @PutMapping
    public Result<?> update(@RequestBody ServiceItem item) {
        return serviceItemService.updateServiceItem(item);
    }

    /**
     * 删除服务项目
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return serviceItemService.deleteServiceItem(id);
    }
}
