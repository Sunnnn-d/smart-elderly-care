package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Bed;

import java.util.Map;

public interface BedService extends IService<Bed> {

    Result<PageResult<Bed>> pageList(Map<String, Object> params);

    Result<?> addBed(Bed bed);

    Result<?> updateBed(Long id, Bed bed);

    Result<?> deleteBed(Long id);

    Result<?> getBedById(Long id);

    Result<?> getBedsByRoomId(Long roomId);

    Result<?> checkIn(Long bedId, Long elderlyId, String elderlyName);

    Result<?> checkOut(Long bedId, String remark);

    Result<?> getAvailableBeds();
}