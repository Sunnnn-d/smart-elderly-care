package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.config.JwtUtil;
import com.smart.elderly.entity.FeeBill;
import com.smart.elderly.entity.AppUser;
import com.smart.elderly.service.FeeBillService;
import com.smart.elderly.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/fee-bill")
@RequiredArgsConstructor
public class FeeBillController {

    private final FeeBillService feeBillService;
    
    private final AppUserService appUserService;
    
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<PageResult<FeeBill>> list(@RequestParam Map<String, Object> params) {
        return feeBillService.pageList(params);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return feeBillService.getBillById(id);
    }

    @GetMapping("/elderly/{elderlyId}")
    public Result<?> getByElderlyId(@PathVariable Long elderlyId) {
        return feeBillService.getBillsByElderlyId(elderlyId);
    }

    @GetMapping("/user/list")
    public Result<?> getByUserId(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        AppUser user = appUserService.getById(userId);
        if (user == null || user.getElderlyId() == null) {
            return Result.success(null);
        }
        return feeBillService.getBillsByElderlyId(user.getElderlyId());
    }

    @GetMapping("/elderly/{elderlyId}/month/{month}")
    public Result<?> getByMonth(@PathVariable Long elderlyId, @PathVariable String month) {
        return feeBillService.getBillByMonth(elderlyId, month);
    }

    @PostMapping
    public Result<?> add(@RequestBody FeeBill bill) {
        return feeBillService.addBill(bill);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FeeBill bill) {
        return feeBillService.updateBill(id, bill);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return feeBillService.deleteBill(id);
    }
}