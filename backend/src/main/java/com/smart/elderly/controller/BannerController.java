package com.smart.elderly.controller;

import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Banner;
import com.smart.elderly.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图控制器
 */
@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 获取启用的轮播图（前端展示用）
     */
    @GetMapping("/public/list")
    public Result<List<Banner>> publicList() {
        return bannerService.listActive();
    }

    /**
     * 获取所有轮播图（后台管理用）
     */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(bannerService.list());
    }

    /**
     * 添加轮播图
     */
    @PostMapping
    public Result<?> add(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success("添加成功");
    }

    /**
     * 更新轮播图
     */
    @PutMapping
    public Result<?> update(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return Result.success("更新成功");
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success("删除成功");
    }
}
