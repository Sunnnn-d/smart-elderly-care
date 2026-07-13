package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ElderlyProfile;
import com.smart.elderly.service.ElderlyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 老人档案管理控制器
 */
@RestController
@RequestMapping("/elderly")
@RequiredArgsConstructor
public class ElderlyProfileController {

    private final ElderlyProfileService elderlyProfileService;

    /**
     * 分页查询老人列表
     */
    @GetMapping("/list")
    public Result<PageResult<ElderlyProfile>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return elderlyProfileService.pageList(pageNum, pageSize, keyword);
    }

    /**
     * 根据ID查询老人信息
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(elderlyProfileService.getById(id));
    }

    /**
     * 添加老人档案
     */
    @PostMapping
    public Result<?> add(@RequestBody ElderlyProfile profile) {
        return elderlyProfileService.addElderly(profile);
    }

    /**
     * 更新老人档案
     */
    @PutMapping
    public Result<?> update(@RequestBody ElderlyProfile profile) {
        return elderlyProfileService.updateElderly(profile);
    }

    /**
     * 删除老人档案
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return elderlyProfileService.deleteElderly(id);
    }
}
