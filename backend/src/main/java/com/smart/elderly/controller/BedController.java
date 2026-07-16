package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Bed;
import com.smart.elderly.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/bed")
@RequiredArgsConstructor
public class BedController {

    private final BedService bedService;

    @GetMapping("/list")
    public Result<PageResult<Bed>> list(@RequestParam Map<String, Object> params) {
        return bedService.pageList(params);
    }

    @GetMapping("/room/{roomId}")
    public Result<?> getByRoomId(@PathVariable Long roomId) {
        return bedService.getBedsByRoomId(roomId);
    }

    @GetMapping("/available")
    public Result<?> getAvailable() {
        return bedService.getAvailableBeds();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return bedService.getBedById(id);
    }

    @PostMapping
    public Result<?> add(@RequestBody Bed bed) {
        return bedService.addBed(bed);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Bed bed) {
        return bedService.updateBed(id, bed);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return bedService.deleteBed(id);
    }

    @PutMapping("/check-in/{bedId}")
    public Result<?> checkIn(@PathVariable Long bedId, 
                             @RequestParam Long elderlyId, 
                             @RequestParam String elderlyName) {
        return bedService.checkIn(bedId, elderlyId, elderlyName);
    }

    @PutMapping("/check-out/{bedId}")
    public Result<?> checkOut(@PathVariable Long bedId, @RequestParam(required = false) String remark) {
        return bedService.checkOut(bedId, remark);
    }
}