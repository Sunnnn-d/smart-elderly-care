package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Room;

import java.util.Map;

public interface RoomService extends IService<Room> {

    Result<PageResult<Room>> pageList(Map<String, Object> params);

    Result<?> addRoom(Room room);

    Result<?> updateRoom(Long id, Room room);

    Result<?> deleteRoom(Long id);

    Result<?> getRoomById(Long id);

    Result<?> getAllRooms();
}