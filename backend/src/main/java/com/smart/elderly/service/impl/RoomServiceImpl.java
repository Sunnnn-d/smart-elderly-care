package com.smart.elderly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Room;
import com.smart.elderly.mapper.RoomMapper;
import com.smart.elderly.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Override
    public Result<PageResult<Room>> pageList(Map<String, Object> params) {
        Integer pageNum = params.get("pageNum") != null ? (Integer) params.get("pageNum") : 1;
        Integer pageSize = params.get("pageSize") != null ? (Integer) params.get("pageSize") : 10;
        
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        if (params.containsKey("roomNumber") && StringUtils.hasText((String) params.get("roomNumber"))) {
            wrapper.like(Room::getRoomNumber, params.get("roomNumber"));
        }
        if (params.containsKey("roomType") && StringUtils.hasText((String) params.get("roomType"))) {
            wrapper.eq(Room::getRoomType, params.get("roomType"));
        }
        if (params.containsKey("status")) {
            wrapper.eq(Room::getStatus, params.get("status"));
        }
        if (params.containsKey("floor")) {
            wrapper.eq(Room::getFloor, params.get("floor"));
        }
        wrapper.orderByDesc(Room::getCreateTime);

        Page<Room> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        PageResult<Room> pageResult = new PageResult<>(
                page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRoom(Room room) {
        this.save(room);
        return Result.success("房间添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateRoom(Long id, Room room) {
        Room existing = this.getById(id);
        if (existing == null) {
            return Result.error("房间不存在");
        }
        room.setId(id);
        this.updateById(room);
        return Result.success("房间更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRoom(Long id) {
        Room room = this.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }
        this.removeById(id);
        return Result.success("房间删除成功");
    }

    @Override
    public Result<?> getRoomById(Long id) {
        Room room = this.getById(id);
        if (room == null) {
            return Result.error("房间不存在");
        }
        return Result.success(room);
    }

    @Override
    public Result<?> getAllRooms() {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getStatus, 1)
               .orderByAsc(Room::getRoomNumber);
        List<Room> rooms = this.list(wrapper);
        return Result.success(rooms);
    }
}