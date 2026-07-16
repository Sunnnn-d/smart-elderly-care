package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.FeeItem;
import com.smart.elderly.service.FeeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fee-item")
@RequiredArgsConstructor
public class FeeItemController {

    private final FeeItemService feeItemService;

    @GetMapping("/list")
    public Result<PageResult<FeeItem>> list(@RequestParam Map<String, Object> params) {
        return feeItemService.pageList(params);
    }

    @GetMapping("/all")
    public Result<?> getAll() {
        return feeItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return feeItemService.getItemById(id);
    }

    @PostMapping
    public Result<?> add(@RequestBody FeeItem item) {
        return feeItemService.addItem(item);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FeeItem item) {
        return feeItemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return feeItemService.deleteItem(id);
    }
}