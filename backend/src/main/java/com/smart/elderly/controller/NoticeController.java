package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Notice;
import com.smart.elderly.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公告资讯控制器
 */
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 分页查询公告（前端展示用）
     */
    @GetMapping("/public/list")
    public Result<PageResult<Notice>> publicList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer type) {
        return noticeService.pageList(pageNum, pageSize, type);
    }

    /**
     * 查询公告详情
     */
    @GetMapping("/public/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    /**
     * 分页查询公告（后台管理用）
     */
    @GetMapping("/list")
    public Result<PageResult<Notice>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer type) {
        return noticeService.pageList(pageNum, pageSize, type);
    }

    /**
     * 添加公告
     */
    @PostMapping
    public Result<?> add(@RequestBody Notice notice) {
        return noticeService.addNotice(notice);
    }

    /**
     * 更新公告
     */
    @PutMapping
    public Result<?> update(@RequestBody Notice notice) {
        return noticeService.updateNotice(notice);
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return noticeService.deleteNotice(id);
    }
}
