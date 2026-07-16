package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Bed;
import com.smart.elderly.mapper.BedMapper;
import com.smart.elderly.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {

    @Override
    public Result<PageResult<Bed>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("roomId")) {
            wrapper.eq(Bed::getRoomId, params.get("roomId"));
        }
        if (params.containsKey("roomNumber") && StringUtils.hasText((String) params.get("roomNumber"))) {
            wrapper.like(Bed::getRoomNumber, params.get("roomNumber"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(Bed::getStatus, params.get("status"));
        }
        if (params.containsKey("elderlyId")) {
            wrapper.eq(Bed::getElderlyId, params.get("elderlyId"));
        }
        if (params.containsKey("elderlyName") && StringUtils.hasText((String) params.get("elderlyName"))) {
            wrapper.like(Bed::getElderlyName, params.get("elderlyName"));
        }
        wrapper.orderByAsc(Bed::getRoomNumber)
               .orderByAsc(Bed::getBedNumber);

        Page<Bed> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<Bed> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addBed(Bed bed) {
        this.save(bed);
        return Result.success("床位添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateBed(Long id, Bed bed) {
        Bed existing = this.getById(id);
        if (existing == null) {
            return Result.error("床位不存在");
        }
        bed.setId(id);
        this.updateById(bed);
        return Result.success("床位更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteBed(Long id) {
        Bed bed = this.getById(id);
        if (bed == null) {
            return Result.error("床位不存在");
        }
        if (bed.getStatus() == 1) {
            return Result.error("该床位有老人入住，无法删除");
        }
        this.removeById(id);
        return Result.success("床位删除成功");
    }

    @Override
    public Result<?> getBedById(Long id) {
        Bed bed = this.getById(id);
        if (bed == null) {
            return Result.error("床位不存在");
        }
        return Result.success(bed);
    }

    @Override
    public Result<?> getBedsByRoomId(Long roomId) {
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bed::getRoomId, roomId)
               .orderByAsc(Bed::getBedNumber);
        List<Bed> beds = this.list(wrapper);
        return Result.success(beds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> checkIn(Long bedId, Long elderlyId, String elderlyName) {
        Bed bed = this.getById(bedId);
        if (bed == null) {
            return Result.error("床位不存在");
        }
        if (bed.getStatus() != 0) {
            return Result.error("该床位当前状态不允许入住");
        }
        bed.setElderlyId(elderlyId);
        bed.setElderlyName(elderlyName);
        bed.setCheckInTime(LocalDateTime.now());
        bed.setStatus(1);
        this.updateById(bed);
        return Result.success("入住成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> checkOut(Long bedId, String remark) {
        Bed bed = this.getById(bedId);
        if (bed == null) {
            return Result.error("床位不存在");
        }
        if (bed.getStatus() != 1) {
            return Result.error("该床位当前状态不允许离院");
        }
        bed.setElderlyId(null);
        bed.setElderlyName(null);
        bed.setCheckOutTime(LocalDateTime.now());
        bed.setStatus(0);
        bed.setRemark(remark);
        this.updateById(bed);
        return Result.success("离院成功");
    }

    @Override
    public Result<?> getAvailableBeds() {
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bed::getStatus, 0)
               .orderByAsc(Bed::getRoomNumber)
               .orderByAsc(Bed::getBedNumber);
        List<Bed> beds = this.list(wrapper);
        return Result.success(beds);
    }
}